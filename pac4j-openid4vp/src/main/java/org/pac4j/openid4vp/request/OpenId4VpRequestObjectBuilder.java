package org.pac4j.openid4vp.request;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.VerifierAttestation;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;
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
public class OpenId4VpRequestObjectBuilder {

    /**
     * The content encryption algorithms accepted for the response, both listed as the high assurance profile
     * requires: "Verifiers MUST list both A128GCM and A256GCM in encrypted_response_enc_values_supported".
     *
     * @see <a href="https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html">
     *     OpenID4VC High Assurance Interoperability Profile</a>
     */
    public static final List<String> ACCEPTED_ENC_VALUES = List.of("A128GCM", "A256GCM");

    protected final OpenId4VpClient client;

    /**
     * <p>Build the signed request object of a transaction.</p>
     *
     * <p>The response comes back on the very endpoint the request object is fetched from: both are told
     * apart by the HTTP method and the posted parameters, so {@code response_uri} and {@code request_uri}
     * hold the same URL.</p>
     *
     * <p>The {@code aud} claim depends on whether the wallet is known beforehand: "the aud claim MUST be equal
     * to the iss (issuer) claim value, when Dynamic Discovery is performed", and "MUST be
     * https://self-issued.me/v2, when Static Discovery metadata is used". No wallet is ever discovered here,
     * the request being handed to whichever wallet the End-User holds, so the symbolic value is used.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being answered
     * @return the serialized signed request object
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-5.8">
     *     OpenID4VP 1.0, aud of a request object</a>
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
        if (transaction.getWalletNonce() != null) {
            // "When received, the Verifier MUST use it as the wallet_nonce value in the signed authorization request object"
            // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#request_uri_method_post
            builder.claim(WALLET_NONCE, transaction.getWalletNonce());
        }
        // the claims a request object carries on top of the protocol parameters
        return builder
            .issuer(client.getConfiguration().computeClientId())
            .audience(REQUEST_OBJECT_AUDIENCE)
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
        // the only response type honoured, of the three OpenID4VP 1.0 defines: "vp_token id_token" adds a self-issued
        // ID token which would need SIOPv2 to be validated, and "code" has the wallet run a token endpoint. Neither
        // is used by the high assurance profile, and neither is offered until the extractor reads what it brings
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#response_type_vp_token
        parameters.put(RESPONSE_TYPE, RESPONSE_TYPE_VP_TOKEN);
        parameters.put(RESPONSE_MODE, configuration.getResponseMode().getValue());
        parameters.put(NONCE, transaction.getNonce());
        // the same test as the configuration applies at initialization: a blank scope, as a properties file
        // binding hands one over, is no scope
        if (isNotBlank(configuration.getScope())) {
            parameters.put(SCOPE, configuration.getScope());
        } else {
            parameters.put(DCQL_QUERY, configuration.getDcqlQuery().toJson());
        }
        parameters.put(CLIENT_METADATA, buildClientMetadata(transaction));
        if (!configuration.getVerifierInfo().isEmpty()) {
            parameters.put(VERIFIER_INFO, configuration.getVerifierInfo().stream().map(VerifierAttestation::toMember).toList());
        }
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
        // the response comes back on the very endpoint the request object is fetched from. Sent even with the
        // redirect_uri prefix, where the client identifier already holds it: "The Verifier MAY omit the redirect_uri
        // Authorization Request parameter (or response_uri when Response Mode direct_post is used)", and the
        // specification's own example of such a request carries both, so a wallet may well read the parameter
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes
        parameters.put(RESPONSE_URI, client.computeRequestUri(ctx.webContext(), transaction.getId()));
        if (transaction.getState() != null) {
            parameters.put(STATE, transaction.getState());
        }
    }

    /**
     * <p>Build the metadata the wallet needs about this verifier: the key to encrypt its response to, the
     * content encryption algorithms accepted, and the credential formats it can verify.</p>
     *
     * <p>A wallet which posted its own metadata to the request URI is answered what it can honour: the
     * formats and the encryption algorithms are narrowed down to those it declared, and the request is
     * refused when nothing is left, rather than sent to fail.</p>
     *
     * @param transaction the transaction being answered
     * @return the client metadata
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_metadata_parameters">
     *     OpenID4VP 1.0, verifier metadata</a>
     */
    protected Map<String, Object> buildClientMetadata(final VpTransaction transaction) {
        val walletMetadata = readWalletMetadata(transaction);
        val metadata = new LinkedHashMap<String, Object>();

        if (transaction.getEncryptionKey() != null) {
            try {
                val publicKey = ECKey.parse(transaction.getEncryptionKey()).toPublicJWK().toJSONObject();
                metadata.put(JWKS, Map.of(KEYS, List.of(publicKey)));
                metadata.put(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED, computeEncValues(walletMetadata, transaction));
            } catch (final ParseException e) {
                throw new OpenId4VpException("unable to publish the response encryption key", e);
            }
        }

        val formats = new LinkedHashMap<String, Object>();
        computeFormats(walletMetadata, transaction).forEach(format -> formats.put(format, Map.of()));
        metadata.put(VP_FORMATS_SUPPORTED, formats);

        return metadata;
    }

    /**
     * <p>The metadata the wallet posted to the request URI, empty when it fetched the request object with
     * a GET.</p>
     *
     * @param transaction the transaction being answered
     * @return the wallet metadata
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#as_metadata_parameters">
     *     OpenID4VP 1.0, wallet metadata</a>
     */
    protected Map<String, Object> readWalletMetadata(final VpTransaction transaction) {
        if (transaction.getWalletMetadata() == null) {
            return Map.of();
        }
        try {
            return JSONObjectUtils.parse(transaction.getWalletMetadata());
        } catch (final ParseException e) {
            throw new OpenId4VpException("the wallet metadata of the transaction " + transaction.getId()
                + " is not a JSON object: " + e.getMessage(), e);
        }
    }

    /**
     * <p>The content encryption algorithms to publish: the accepted ones, narrowed down to those the wallet
     * declared when it did. "If the Wallet supports encrypting the Authorization Response, it SHOULD specify
     * supported encryption algorithms using the authorization_encryption_alg_values_supported and
     * authorization_encryption_enc_values_supported parameters".</p>
     *
     * @param walletMetadata the metadata the wallet posted, empty when it posted none
     * @param transaction the transaction being answered
     * @return the enc values
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#as_metadata_parameters">
     *     OpenID4VP 1.0, wallet metadata</a>
     */
    protected List<String> computeEncValues(final Map<String, Object> walletMetadata, final VpTransaction transaction) {
        final List<String> walletEncValues;
        try {
            walletEncValues = JSONObjectUtils.getStringList(walletMetadata, AUTHORIZATION_ENCRYPTION_ENC_VALUES_SUPPORTED);
        } catch (final ParseException e) {
            throw new OpenId4VpException("the wallet metadata member " + AUTHORIZATION_ENCRYPTION_ENC_VALUES_SUPPORTED
                + " must be an array of strings: " + e.getMessage(), e);
        }
        if (walletEncValues == null) {
            return ACCEPTED_ENC_VALUES;
        }
        val encValues = ACCEPTED_ENC_VALUES.stream().filter(walletEncValues::contains).toList();
        if (encValues.isEmpty()) {
            throw new OpenId4VpException("the wallet encrypts its response with none of the accepted content encryption "
                + "algorithms " + ACCEPTED_ENC_VALUES + ", but with " + walletEncValues + ": " + transaction.getId());
        }
        return encValues;
    }

    /**
     * <p>The credential formats to publish: those a verifier is registered for, narrowed down to those the
     * wallet declared in its {@code vp_formats_supported} when it did.</p>
     *
     * @param walletMetadata the metadata the wallet posted, empty when it posted none
     * @param transaction the transaction being answered
     * @return the format identifiers
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#as_metadata_parameters">
     *     OpenID4VP 1.0, wallet metadata</a>
     */
    protected List<String> computeFormats(final Map<String, Object> walletMetadata, final VpTransaction transaction) {
        val verifiedFormats = client.getConfiguration().getCredentialVerifiers().keySet().stream()
            .map(CredentialFormat::getValue).toList();
        final Map<String, Object> walletFormats;
        try {
            walletFormats = JSONObjectUtils.getJSONObject(walletMetadata, VP_FORMATS_SUPPORTED);
        } catch (final ParseException e) {
            throw new OpenId4VpException("the wallet metadata member " + VP_FORMATS_SUPPORTED
                + " must be a JSON object: " + e.getMessage(), e);
        }
        if (walletFormats == null) {
            return verifiedFormats;
        }
        val formats = verifiedFormats.stream().filter(walletFormats::containsKey).toList();
        if (formats.isEmpty()) {
            throw new OpenId4VpException("the wallet presents none of the credential formats this verifier verifies "
                + verifiedFormats + ", but " + walletFormats.keySet() + ": " + transaction.getId());
        }
        return formats;
    }
}
