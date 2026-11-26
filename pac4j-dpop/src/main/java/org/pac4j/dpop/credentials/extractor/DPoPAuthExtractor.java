package org.pac4j.dpop.credentials.extractor;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPIssuer;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPProtectedResourceRequestVerifier;
import com.nimbusds.oauth2.sdk.dpop.verifiers.InvalidDPoPProofException;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.store.ConcurrentMapStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.dpop.proof.DPoPNonceManager;
import org.pac4j.dpop.proof.DPoPProofReplayChecker;
import org.pac4j.dpop.util.DPoPProtocolMessages;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.pac4j.dpop.util.DPoPConstants.*;

/**
 * To extract a DPoP-bound access token (RFC 9449, section 7.1): the access token is sent in the
 * {@code Authorization: DPoP} header and is only extracted when the DPoP proof sent in the {@code DPoP} header is valid.
 *
 * <p>The proof is checked according to RFC 9449, section 4.3: its claims (method, URL, creation time), its
 * signature, the hash of the access token ("ath" claim), the key the access token is bound to ("jkt" confirmation)
 * and its single use.</p>
 *
 * <p>The access token itself is not validated here: this is the job of the authenticator of the client.</p>
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
@ToString(exclude = {"nonceSecret", "tokenExtractor", "verifier", "nonceManager"})
public class DPoPAuthExtractor extends InitializableObject implements CredentialsExtractor {

    /** The algorithms accepted for the signature of the proofs. */
    private Set<JWSAlgorithm> jwsAlgorithms = new LinkedHashSet<>(List.of(JWSAlgorithm.ES256, JWSAlgorithm.ES384,
        JWSAlgorithm.ES512, JWSAlgorithm.PS256, JWSAlgorithm.PS384, JWSAlgorithm.PS512, JWSAlgorithm.RS256,
        JWSAlgorithm.RS384, JWSAlgorithm.RS512));

    /** The maximum age of a proof, from its "iat" claim. It cannot be less than the clock skew. */
    private int maxAgeSeconds = 60;

    /** The tolerance for a proof whose "iat" claim is in the future. */
    private int clockSkewSeconds = 60;

    /**
     * Where the identifiers of the proofs are saved to refuse a proof already used: by default, a
     * {@link ConcurrentMapStore} keeping them as long as their proof would be accepted.
     */
    private Store<String, Boolean> store;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ConcurrentMapStore<String, Boolean> defaultStore;

    /** The secret generating the nonces: when defined, a nonce is required in the proofs. */
    private String nonceSecret;

    /** The maximum age of a nonce. */
    private int nonceMaxAgeSeconds = 300;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private HeaderExtractor tokenExtractor;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DPoPProtectedResourceRequestVerifier verifier;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DPoPNonceManager nonceManager;

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertTrue(jwsAlgorithms != null && !jwsAlgorithms.isEmpty(), "jwsAlgorithms cannot be empty");
        CommonHelper.assertTrue(maxAgeSeconds >= clockSkewSeconds, "maxAgeSeconds cannot be less than clockSkewSeconds");

        tokenExtractor = new HeaderExtractor(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DPOP_HEADER_PREFIX);
        tokenExtractor.setTrimValue(true);
        // A proof created in the future, within the clock skew, is accepted until it reaches its maximum age.
        val retentionSeconds = maxAgeSeconds + clockSkewSeconds;
        if (store == null) {
            defaultStore = new ConcurrentMapStore<>(retentionSeconds, TimeUnit.SECONDS);
            store = defaultStore;
        } else if (store == defaultStore) {
            defaultStore.setTimeout(retentionSeconds);
        } else {
            defaultStore = null;
        }
        verifier = new DPoPProtectedResourceRequestVerifier(jwsAlgorithms, clockSkewSeconds, maxAgeSeconds,
            new DPoPProofReplayChecker(store));
        nonceManager = CommonHelper.isBlank(nonceSecret) ? null : new DPoPNonceManager(nonceSecret, nonceMaxAgeSeconds);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        init();

        val credentials = tokenExtractor.extract(ctx);
        if (credentials.isPresent()) {
            checkProof(ctx, ((TokenCredentials) credentials.get()).getToken());
        }
        return credentials;
    }

    /**
     * Check the DPoP proof sent with the access token (RFC 9449, section 4.3).
     *
     * @param ctx the call context
     * @param accessToken the access token
     */
    protected void checkProof(final CallContext ctx, final String accessToken) {
        val webContext = ctx.webContext();
        val method = webContext.getRequestMethod();
        val url = webContext.getRequestURL();
        val header = webContext.getRequestHeader(DPOP_HEADER)
            .or(() -> webContext.getRequestHeader(DPOP_HEADER.toLowerCase(Locale.ROOT)))
            .orElseThrow(() -> refuse(webContext, INVALID_DPOP_PROOF_ERROR, "Missing DPoP proof", null));
        DPoPProtocolMessages.receivedProof(method, url);

        final SignedJWT proof;
        final String nonce;
        try {
            proof = SignedJWT.parse(header);
            nonce = proof.getJWTClaimsSet().getStringClaim("nonce");
        } catch (final ParseException e) {
            throw refuse(webContext, INVALID_DPOP_PROOF_ERROR, "Malformed DPoP proof: " + e.getMessage(), e);
        }
        checkNonce(webContext, nonce);

        val confirmation = retrieveConfirmation(ctx, accessToken);
        if (confirmation == null) {
            throw refuse(webContext, INVALID_TOKEN_ERROR, "The access token is not bound to a DPoP key", null);
        }

        try {
            // the nonce has been checked already or, when none is required, is ignored: the verifier is asked to accept it as it is.
            // The proofs are single use per key: the one the access token is bound to
            verifier.verify(method, URI.create(url), new DPoPIssuer(confirmation.getValue().toString()), proof,
                new DPoPAccessToken(accessToken), confirmation, CommonHelper.isBlank(nonce) ? null : Set.of(new Nonce(nonce)), null);
        } catch (final InvalidDPoPProofException | JOSEException e) {
            throw refuse(webContext, INVALID_DPOP_PROOF_ERROR, e.getMessage(), e);
        } catch (final AccessTokenValidationException e) {
            throw refuse(webContext, INVALID_TOKEN_ERROR, e.getMessage(), e);
        }

        LOGGER.debug("DPoP proof validated for method {} with algorithm {}", method, proof.getHeader().getAlgorithm());

        // a nonce getting old is renewed in the response to the accepted request (RFC 9449, section 8.2)
        if (nonceManager != null && nonceManager.shouldBeRenewed(nonce)) {
            provideNonce(webContext, false);
        }
    }

    /**
     * When a secret is defined, require in the proof a nonce generated with it: otherwise, the request is refused
     * with a new nonce, for the client to send it again (RFC 9449, section 9).
     *
     * @param webContext the web context
     * @param nonce the nonce of the proof (can be null)
     */
    protected void checkNonce(final WebContext webContext, final String nonce) {
        if (nonceManager != null && (nonce == null || !nonceManager.isValid(nonce))) {
            provideNonce(webContext, true);
            throw refuse(webContext, USE_DPOP_NONCE_ERROR, "A valid nonce is required in the DPoP proof", null);
        }
    }

    private void provideNonce(final WebContext webContext, final boolean required) {
        val nonce = nonceManager.generate(Instant.now());
        webContext.setResponseHeader(DPOP_NONCE_HEADER, nonce);
        DPoPProtocolMessages.sentNonce(nonce, required);
    }

    /**
     * Retrieve the key the access token is bound to: by default, from the "cnf" claim of a JWT access token and
     * its "jkt" member (RFC 9449, section 6.1).
     *
     * <p>The claim is read before the access token is validated: the binding only holds if the authenticator
     * of the client checks the signature of the access token afterwards.</p>
     *
     * <p>Override this method for the access tokens which are not JWTs, whose binding is returned by the
     * introspection endpoint of the authorization server (RFC 9449, section 6.2).</p>
     *
     * @param ctx the call context
     * @param accessToken the access token
     * @return the confirmation, or null when the access token is not bound to a key
     */
    protected JWKThumbprintConfirmation retrieveConfirmation(final CallContext ctx, final String accessToken) {
        try {
            val claims = JWTParser.parse(accessToken).getJWTClaimsSet();
            return claims == null ? null : JWKThumbprintConfirmation.parse(claims);
        } catch (final ParseException e) {
            return null;
        }
    }

    /**
     * Refuse the request, saving the error as a request attribute for the challenge built by the client.
     */
    private CredentialsException refuse(final WebContext webContext, final String error, final String message,
                                        final Throwable cause) {
        webContext.setRequestAttribute(ERROR_REQUEST_ATTRIBUTE, error);
        return new CredentialsException(message, cause);
    }
}
