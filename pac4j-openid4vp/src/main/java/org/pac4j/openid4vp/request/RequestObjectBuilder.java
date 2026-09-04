package org.pac4j.openid4vp.request;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Builds the signed request object a wallet fetches, telling it what is asked and how to answer.
 *
 * <p>It is built when the wallet asks for it rather than when the transaction is opened, so that the
 * transaction stays small in the store, nothing is signed for a request nobody comes for, and a wallet
 * posting its own metadata on the request URI can be answered a request object fit for it.</p>
 *
 * <p>The dates however come from the transaction, not from the moment of the build: the wallet must read the
 * very expiration the store honours, so that there is only ever one lifetime.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
public class RequestObjectBuilder {

    protected final OpenId4VpClient client;

    /**
     * <p>Build the signed request object of a transaction.</p>
     *
     * <p>The response comes back on the very endpoint the request object is fetched from: both are told
     * apart by the HTTP method and the posted parameters, so {@code response_uri} and {@code request_uri}
     * hold the same URL.</p>
     *
     * <p>The {@code aud} claim is deliberately left out: its expected value moved across the drafts, and an
     * audience the wallet does not expect is worse than none. Check it against the version targeted.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being answered
     * @return the serialized signed request object
     */
    public String build(final CallContext ctx, final VpTransaction transaction) {
        val configuration = client.getConfiguration();
        return JwkHelper.buildSignedJwt(buildClaims(ctx, transaction).build(),
            configuration.getRequestObjectSigningKey(), configuration.computeRequestObjectSigningAlgorithm(),
            REQUEST_OBJECT_TYPE, configuration.publishesCertificateChain());
    }

    /**
     * <p>Assemble the claims of the request object: those every binding shares, then those of the binding.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being answered
     * @return the claims
     */
    protected JWTClaimsSet.Builder buildClaims(final CallContext ctx, final VpTransaction transaction) {
        val builder = new JWTClaimsSet.Builder();
        buildParameters(ctx, transaction).forEach(builder::claim);
        // the claims a request object carries on top of the protocol parameters
        return builder
            .issuer(client.getConfiguration().computeClientId())
            .issueTime(Date.from(transaction.getCreatedAt()))
            .expirationTime(Date.from(transaction.getExpiresAt()));
    }

    /**
     * <p>Assemble the authorization request parameters, which a signed request carries as claims of its
     * request object and an unsigned one carries in the wallet URL.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being answered
     * @return the parameters
     */
    public Map<String, Object> buildParameters(final CallContext ctx, final VpTransaction transaction) {
        val configuration = client.getConfiguration();
        val parameters = new LinkedHashMap<String, Object>();
        parameters.put(CLIENT_ID, configuration.computeClientId(client.computeRequestUri(ctx.webContext(), transaction.getId())));
        parameters.put(RESPONSE_TYPE, RESPONSE_TYPE_VP_TOKEN);
        parameters.put(RESPONSE_MODE, configuration.getResponseMode().getValue());
        parameters.put(NONCE, transaction.getNonce());
        parameters.put(DCQL_QUERY, parseDcqlQuery(configuration.getDcqlQuery()));
        parameters.put(CLIENT_METADATA, buildClientMetadata(transaction));
        addBindingParameters(ctx, transaction, parameters);
        return parameters;
    }

    /**
     * <p>Add the parameters which depend on how the wallet is reached: where to post the answer when it is
     * invoked by a URL, which origins are expected when it is reached through the browser.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being answered
     * @param parameters the parameters being assembled
     */
    protected void addBindingParameters(final CallContext ctx, final VpTransaction transaction,
                                        final Map<String, Object> parameters) {
        // the response comes back on the very endpoint the request object is fetched from
        parameters.put(RESPONSE_URI, client.computeRequestUri(ctx.webContext(), transaction.getId()));
        if (transaction.getState() != null) {
            parameters.put(STATE, transaction.getState());
        }
    }

    /**
     * <p>Build the metadata the wallet needs about this verifier: the key to encrypt its response to, and
     * the credential formats asked for.</p>
     *
     * @param transaction the transaction being answered
     * @return the client metadata
     */
    protected Map<String, Object> buildClientMetadata(final VpTransaction transaction) {
        val configuration = client.getConfiguration();
        val metadata = new LinkedHashMap<String, Object>();

        if (transaction.getEncryptionKey() != null) {
            try {
                val publicKey = ECKey.parse(transaction.getEncryptionKey()).toPublicJWK().toJSONObject();
                metadata.put(JWKS, Map.of(KEYS, List.of(publicKey)));
                metadata.put(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED, List.of("A128GCM"));
            } catch (final ParseException e) {
                throw new OpenId4VpException("unable to publish the response encryption key", e);
            }
        }

        val formats = new LinkedHashMap<String, Object>();
        configuration.getSupportedFormats().forEach(format -> formats.put(format.getValue(), Map.of()));
        metadata.put(VP_FORMATS_SUPPORTED, formats);

        return metadata;
    }

    /**
     * <p>Read the DCQL query, held as a raw JSON string until it gets a type of its own.</p>
     *
     * @param dcqlQuery the query
     * @return the query as a JSON object
     */
    protected Map<String, Object> parseDcqlQuery(final String dcqlQuery) {
        try {
            return JSONObjectUtils.parse(dcqlQuery);
        } catch (final ParseException e) {
            throw new OpenId4VpException("unable to read the DCQL query: " + dcqlQuery, e);
        }
    }
}
