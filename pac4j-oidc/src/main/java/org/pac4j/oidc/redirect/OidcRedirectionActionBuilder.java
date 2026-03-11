package org.pac4j.oidc.redirect;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.config.OidcConfigurationContext;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.util.JwkHelper;
import org.pac4j.oidc.util.OidcHelper;

import java.util.*;
import java.util.stream.Collectors;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * Redirect to the OpenID Connect provider.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class OidcRedirectionActionBuilder extends InitializableObject implements RedirectionActionBuilder {

    protected OidcClient client;

    private JWSAlgorithm signingAlg;

    private JWK signingKey;

    /**
     * <p>Constructor for OidcRedirectionActionBuilder.</p>
     *
     * @param client a {@link OidcClient} object
     */
    public OidcRedirectionActionBuilder(final OidcClient client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
    }

    @Override
    protected void internalInit(boolean forceReinit) {
        val config = client.getConfiguration();
        val requestedAlg = config.getRequestObjectSigningAlgorithm();
        if (requestedAlg != null || config.isFederation()) {
            assertTrue(config.getRpJwks().isDefined(), "config.rpJwks must be defined to sign request objects");

            val opAlgs = config.getOpMetadataResolver().load().getRequestObjectJWSAlgs();
            val matchedAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("Request Object", requestedAlg, opAlgs);
            assertTrue(matchedAlgs != null && matchedAlgs.size() >= 1, "At least one algorithm is expected");

            signingAlg = matchedAlgs.get(0);
            signingKey = JwkHelper.loadJwkFromOrCreateJwks(config.getRpJwks());
        }
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        init();

        val webContext = ctx.webContext();

        val config = client.getConfiguration();
        val configContext = new OidcConfigurationContext(webContext, config);
        val params = buildParams(webContext);

        val computedCallbackUrl = client.computeFinalCallbackUrl(webContext);
        params.main().put(OidcConfiguration.REDIRECT_URI, computedCallbackUrl);

        addStateAndNonceParameters(ctx, params);

        var maxAge = configContext.getMaxAge();
        if (maxAge != null) {
            params.main().put(OidcConfiguration.MAX_AGE, maxAge.toString());
        }
        if (configContext.isForceAuthn()) {
            params.main().put(OidcConfiguration.PROMPT, "login");
            params.main().put(OidcConfiguration.MAX_AGE, "0");
        }
        if (configContext.isPassive()) {
            params.main().put(OidcConfiguration.PROMPT, "none");
        }

        val newParams = new HashMap<String, String>();
        newParams.putAll(params.extra());
        if (signingAlg != null && signingKey != null) {
            val now = new Date();
            val claims = new JWTClaimsSet.Builder()
                .issuer(config.getClientId())
                .audience(config.getOpMetadataResolver().load().getIssuer().getValue())
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 60_000))
                .jwtID(UUID.randomUUID().toString());
            for (val param : params.main().keySet()) {
                claims.claim(param, params.main().get(param));
            }
            val request = JwkHelper.buildSignedJwt(claims.build(), signingKey, signingAlg, "oauth.authz.req+jwt");
            newParams.put("request", request);
        } else {
            newParams.putAll(params.main());
        }

        val location = buildAuthenticationRequestUrl(newParams);
        LOGGER.debug("Authentication request url: {}", location);

        return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, location));
    }

    /**
     * <p>buildParams.</p>
     *
     * @param webContext a {@link WebContext} object
     * @return a {@link Map} object
     */
    protected Params buildParams(final WebContext webContext) {
        val configContext = new OidcConfigurationContext(webContext, client.getConfiguration());

        val authParams = new Params(new HashMap<>(), new HashMap<>());
        val scope = configContext.getScope().replace(",", " ");
        authParams.main().put(OidcConfiguration.SCOPE, scope);
        authParams.extra().put(OidcConfiguration.SCOPE, scope);
        val responseType = configContext.getResponseType();
        authParams.main().put(OidcConfiguration.RESPONSE_TYPE, responseType);
        authParams.extra().put(OidcConfiguration.RESPONSE_TYPE, responseType);
        authParams.main().put(OidcConfiguration.RESPONSE_MODE, configContext.getResponseMode());
        authParams.main().putAll(configContext.getCustomParams());
        val clientId = configContext.getConfiguration().getClientId();
        authParams.main().put(OidcConfiguration.CLIENT_ID, clientId);
        authParams.extra().put(OidcConfiguration.CLIENT_ID, clientId);
        val loginHint = configContext.getConfiguration().getLoginHint();
        if (loginHint != null && !loginHint.isEmpty()) {
            authParams.main().put(OidcConfiguration.LOGIN_HINT, loginHint);
        }

        return authParams;
    }

    protected void addStateAndNonceParameters(final CallContext ctx, final Params params) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        // Init state for CSRF mitigation
        if (client.getConfiguration().isWithState()) {
            val state = new State(client.getConfiguration().getStateGenerator().generateValue(ctx));
            params.main().put(OidcConfiguration.STATE, state.getValue());
            sessionStore.set(webContext, client.getStateSessionAttributeName(), state);
        }

        // Init nonce for replay attack mitigation
        if (client.getConfiguration().isUseNonce()) {
            val nonce = new Nonce();
            params.main().put(OidcConfiguration.NONCE, nonce.getValue());
            sessionStore.set(webContext, client.getNonceSessionAttributeName(), nonce.getValue());
        }

        var pkceMethod = client.getConfiguration().findPkceMethod();
        if (pkceMethod != null) {
            val verifier = new CodeVerifier(client.getConfiguration().getCodeVerifierGenerator().generateValue(ctx));
            sessionStore.set(webContext, client.getCodeVerifierSessionAttributeName(), verifier);
            params.main().put(OidcConfiguration.CODE_CHALLENGE, CodeChallenge.compute(pkceMethod, verifier).getValue());
            params.main().put(OidcConfiguration.CODE_CHALLENGE_METHOD, pkceMethod.getValue());
        }
    }

    protected String buildAuthenticationRequestUrl(final Map<String, String> params) {
        // Build authentication request query string
        String queryString;
        try {
            val parameters = params.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));
            queryString = AuthenticationRequest.parse(parameters).toQueryString();
            queryString = queryString.replaceAll("\\+", "%20");
        } catch (final Exception e) {
            throw new OidcException(e);
        }
        return client.getConfiguration().getOpMetadataResolver().load().getAuthorizationEndpointURI().toString() + '?' + queryString;
    }
}

