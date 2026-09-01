package org.pac4j.openid4vp.redirect;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.security.cert.CertificateEncodingException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Opens a presentation request and invokes the wallet.
 *
 * <p>Whatever the invocation mode, a transaction is created first and its identifier saved in the session:
 * it is the only thing tying the browser to the presentation, as the wallet legs carry no session.</p>
 *
 * <p>Whatever the mode, the outcome is a URL and nothing else: pac4j renders no page. The application
 * decides what to do with it &mdash; follow it to hand over to a wallet on the same device, display it as a
 * QR code for a wallet on another device, or fetch the request object from it to feed the browser digital
 * credentials API. It reads that URL either from the redirection itself, or from the {@code Location} header
 * of the unauthorized response its AJAX call receives.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
@Slf4j
public class OpenId4VpRedirectionActionBuilder implements RedirectionActionBuilder {

    private final OpenId4VpClient client;

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val configuration = client.getConfiguration();
        val transaction = createTransaction(ctx);
        configuration.getTransactionStore().set(transaction.getId(), transaction);
        ctx.sessionStore().set(ctx.webContext(), SESSION_TRANSACTION_ID, transaction.getId());

        val url = switch (configuration.getInvocationMode()) {
            case DIGITAL_CREDENTIALS_API -> computeRequestUri(ctx, transaction);
            case CUSTOM_SCHEME -> computeWalletUrl(ctx, transaction);
        };
        LOGGER.debug("transaction {} opened, handing over: {}", transaction.getId(), url);
        LOGGER.trace("request object of the transaction {}: {}", transaction.getId(), transaction.getRequestObject());
        return Optional.of(new FoundAction(url));
    }

    /**
     * <p>Open a transaction and build the signed request object it will serve.</p>
     *
     * @param ctx the context
     * @return the transaction
     */
    protected VpTransaction createTransaction(final CallContext ctx) {
        val configuration = client.getConfiguration();
        val now = Instant.now();
        val transaction = new VpTransaction()
            .setId(configuration.getTransactionIdGenerator().generateValue(ctx))
            .setNonce(configuration.getNonceGenerator().generateValue(ctx))
            .setCreatedAt(now)
            .setExpiresAt(now.plus(configuration.getTransactionLifetimeSeconds(), ChronoUnit.SECONDS));
        transaction.setEncryptionKey(buildEncryptionKey());
        transaction.setRequestObject(buildRequestObject(ctx, transaction));
        return transaction;
    }

    /**
     * <p>Generate the key the wallet encrypts its response to, as a JWK holding its private part.</p>
     *
     * <p>A key is generated for each transaction rather than shared, so that a response can only ever be
     * read for the request it answers. Only the public part is published in the request object.</p>
     *
     * @return the key, or null when the response is not encrypted
     */
    protected String buildEncryptionKey() {
        if (client.getConfiguration().getResponseMode() != ResponseMode.DIRECT_POST_JWT) {
            return null;
        }
        try {
            return new ECKeyGenerator(Curve.P_256)
                .keyID(UUID.randomUUID().toString())
                .keyUse(KeyUse.ENCRYPTION)
                .algorithm(JWEAlgorithm.ECDH_ES)
                .generate()
                .toJSONString();
        } catch (final JOSEException e) {
            throw new OpenId4VpException("unable to generate the response encryption key", e);
        }
    }

    /**
     * <p>Build the signed request object served to the wallet.</p>
     *
     * <p>The response comes back on the very same endpoint the request object is fetched from: both are
     * told apart by the HTTP method and the posted parameters, so {@code response_uri} and
     * {@code request_uri} hold the same URL.</p>
     *
     * <p>The {@code aud} claim is deliberately left out: its expected value moved across the drafts, and an
     * audience the wallet does not expect is worse than none. Check it against the version targeted.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being opened
     * @return the serialized signed request object
     */
    protected String buildRequestObject(final CallContext ctx, final VpTransaction transaction) {
        val configuration = client.getConfiguration();
        val clientId = configuration.computeClientId();
        val responseUri = computeRequestUri(ctx, transaction);

        val builder = new JWTClaimsSet.Builder()
            .issuer(clientId)
            .claim(CLIENT_ID, clientId)
            .claim(RESPONSE_TYPE, RESPONSE_TYPE_VP_TOKEN)
            .claim(RESPONSE_MODE, configuration.getResponseMode().getValue())
            .claim(RESPONSE_URI, responseUri)
            .claim(NONCE, transaction.getNonce())
            .claim(DCQL_QUERY, parseDcqlQuery(configuration.getDcqlQuery()))
            .claim(CLIENT_METADATA, buildClientMetadata(transaction))
            .issueTime(Date.from(transaction.getCreatedAt()))
            .expirationTime(Date.from(transaction.getExpiresAt()));
        if (transaction.getState() != null) {
            builder.claim(STATE, transaction.getState());
        }

        val signedRequestObject = configuration.getRequestObjectSignatureConfiguration()
            .sign(buildRequestObjectHeader(), builder.build());
        return signedRequestObject.serialize();
    }

    /**
     * <p>Build the header of the request object: the type the wallet expects, and the relying party
     * certificate chain when the client identifier is bound to it.</p>
     *
     * @return the header
     */
    protected JWSHeader buildRequestObjectHeader() {
        val configuration = client.getConfiguration();
        val builder = new JWSHeader.Builder(configuration.getRequestObjectSignatureConfiguration().getAlgorithm())
            .type(new JOSEObjectType(REQUEST_OBJECT_TYPE));

        val prefix = configuration.getClientIdPrefix();
        if (prefix == ClientIdPrefix.X509_SAN_DNS || prefix == ClientIdPrefix.X509_HASH) {
            val chain = new ArrayList<Base64>();
            try {
                for (val certificate : configuration.getRelyingPartyCertificateChain()) {
                    chain.add(Base64.encode(certificate.getEncoded()));
                }
            } catch (final CertificateEncodingException e) {
                throw new OpenId4VpException("unable to encode the relying party certificate chain", e);
            }
            builder.x509CertChain(chain);
        }
        return builder.build();
    }

    /**
     * <p>Build the metadata the wallet needs about this verifier: the key to encrypt its response to, and
     * the credential formats asked for.</p>
     *
     * @param transaction the transaction being opened
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

    /**
     * <p>The URL the wallet fetches the request object from: the regular callback endpoint, qualified by
     * the transaction identifier.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being opened
     * @return the request URI
     */
    protected String computeRequestUri(final CallContext ctx, final VpTransaction transaction) {
        val callbackUrl = client.computeFinalCallbackUrl(ctx.webContext());
        return CommonHelper.addParameter(callbackUrl, VP_TRANSACTION_ID, transaction.getId());
    }

    /**
     * <p>The custom scheme deep link a wallet answers to: it only carries the verifier identifier and the
     * request URI, the wallet fetching the request object itself.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being opened
     * @return the wallet URL
     */
    protected String computeWalletUrl(final CallContext ctx, final VpTransaction transaction) {
        val configuration = client.getConfiguration();
        val url = CommonHelper.addParameter(configuration.getWalletScheme(), CLIENT_ID, configuration.computeClientId());
        return CommonHelper.addParameter(url, REQUEST_URI, computeRequestUri(ctx, transaction));
    }
}
