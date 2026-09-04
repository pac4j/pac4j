package org.pac4j.openid4vp.redirect;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    protected final OpenId4VpClient client;

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val configuration = client.getConfiguration();
        val transaction = createTransaction(ctx);
        configuration.getTransactionStore().set(transaction.getId(), transaction);
        ctx.sessionStore().set(ctx.webContext(), SESSION_TRANSACTION_ID, transaction.getId());

        val url = computeWalletUrl(ctx, transaction);
        LOGGER.debug("transaction {} opened, handing over: {}", transaction.getId(), url);
        return Optional.of(new FoundAction(url));
    }

    /**
     * <p>Open a transaction. The request object itself is only built when the wallet asks for it.</p>
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
     * <p>The custom scheme deep link a wallet answers to: it only carries the verifier identifier and the
     * request URI, the wallet fetching the request object itself.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being opened
     * @return the wallet URL
     */
    protected String computeWalletUrl(final CallContext ctx, final VpTransaction transaction) {
        val configuration = client.getConfiguration();
        if (configuration.getClientIdPrefix().isSignedRequest()) {
            val url = CommonHelper.addParameter(configuration.getWalletScheme(), CLIENT_ID, configuration.computeClientId());
            return CommonHelper.addParameter(url, REQUEST_URI, client.computeRequestUri(ctx.webContext(), transaction.getId()));
        }
        return computeUnsignedWalletUrl(ctx, transaction);
    }

    /**
     * <p>The wallet URL of a request which cannot be signed: the parameters travel in it, since there is no
     * request object to fetch.</p>
     *
     * <p>The URL grows accordingly, the client metadata and the DCQL query being carried whole. That is the
     * price of a prefix for which the wallet has no key to trust.</p>
     *
     * @param ctx the context
     * @param transaction the transaction being opened
     * @return the wallet URL
     */
    protected String computeUnsignedWalletUrl(final CallContext ctx, final VpTransaction transaction) {
        var url = client.getConfiguration().getWalletScheme();
        for (val parameter : client.getRequestObjectBuilder().buildParameters(ctx, transaction).entrySet()) {
            val value = parameter.getValue();
            url = CommonHelper.addParameter(url, parameter.getKey(),
                value instanceof Map ? JSONObjectUtils.toJSONString((Map<String, ?>) value) : String.valueOf(value));
        }
        return url;
    }
}
