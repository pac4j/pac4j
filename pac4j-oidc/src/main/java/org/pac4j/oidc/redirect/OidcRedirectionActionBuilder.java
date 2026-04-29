package org.pac4j.oidc.redirect;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.PushedAuthorizationRequest;
import com.nimbusds.oauth2.sdk.PushedAuthorizationResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
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
import org.pac4j.core.util.*;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.config.OidcConfigurationContext;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.metadata.OidcFederationOpMetadataResolver;
import org.pac4j.oidc.util.OidcHelper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.oidc.config.OidcConfiguration.CLIENT_ID;
import static org.pac4j.oidc.config.OidcConfiguration.REDIRECT_URI;

/**
 * Redirect to the OpenID Connect provider.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class OidcRedirectionActionBuilder extends InitializableObject implements RedirectionActionBuilder {
    public static final Announcement ANNOUNCE_WITH_STATE_DISABLED = new Announcement(
        "Be careful when disabling 'withState': this disables state validation and can expose the client to CSRF or "
            + "mix-up style attacks.");

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

        val reqObjSigningRequired = requestedAlg != null;
        val reqObjSigningMandatoryForFederation = config.isFederation() && !config.isPushedAuthorizationRequest()
            && !ClientAuthenticationMethod.PRIVATE_KEY_JWT.equals(config.getClientAuthenticationMethod());
        if (reqObjSigningRequired || reqObjSigningMandatoryForFederation) {
            assertTrue(config.getRpJwks().isDefined(), "config.rpJwks must be defined to sign request objects");

            config.ensuresMetadataResolverInitialized();
            val opAlgs = config.getOpMetadataResolver().load().getRequestObjectJWSAlgs();
            val matchedAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("Request Object", requestedAlg, opAlgs);
            assertTrue(matchedAlgs != null && matchedAlgs.size() >= 1, "At least one algorithm is expected");

            signingAlg = matchedAlgs.get(0);
            LOGGER.debug("Algorithm used for request object signing: {}", signingAlg);
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
        params.requestObject().put(REDIRECT_URI, computedCallbackUrl);

        addStateAndNonceParameters(ctx, params);

        var maxAge = configContext.getMaxAge();
        if (maxAge != null) {
            params.requestObject().put(OidcConfiguration.MAX_AGE, maxAge.toString());
        }
        if (configContext.isForceAuthn()) {
            params.requestObject().put(OidcConfiguration.PROMPT, "login");
            params.requestObject().put(OidcConfiguration.MAX_AGE, "0");
        }
        if (configContext.isPassive()) {
            params.requestObject().put(OidcConfiguration.PROMPT, "none");
        }

        val newParams = new HashMap<String, String>();
        newParams.putAll(params.url());
        if (signingAlg != null && signingKey != null) {
            val now = new Date();
            val claims = new JWTClaimsSet.Builder()
                .issuer(config.getClientId())
                .audience(config.getOpMetadataResolver().load().getIssuer().getValue())
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 60_000))
                .jwtID(UUID.randomUUID().toString());
            for (val param : params.requestObject().keySet()) {
                claims.claim(param, params.requestObject().get(param));
            }
            if (config.getFederation().isSendTrustChain()) {
                val resolver = config.getOpMetadataResolver();
                if (resolver instanceof OidcFederationOpMetadataResolver federationOpMetadataResolver) {
                    val trustChain = federationOpMetadataResolver.getTrustChain();
                    if (trustChain != null) {
                        LOGGER.debug("Adding trust_chain with {} ES", trustChain.size());
                        claims.claim("trust_chain", trustChain);
                    }
                }
            }
            val builtClaims = claims.build();
            if (LOGGER.isDebugEnabled()) {
                val map = builtClaims.getClaims();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.debug("Request Object claims: {}", map.entrySet());
                } else {
                    LOGGER.debug("Request Object claim names: {}", map.keySet());
                }
            }
            val request = JwkHelper.buildSignedJwt(builtClaims, signingKey, signingAlg, "oauth-authz-req+jwt");
            newParams.put("request", request);
        } else {
            newParams.putAll(params.requestObject());
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("Authz parameters: {}", newParams.entrySet());
        } else {
            LOGGER.debug("Authz parameter names: {}", newParams.keySet());
        }

        val location = buildAuthenticationRequestUrl(newParams);
        LOGGER.debug("Authentication request URL: {}", location);

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
        authParams.requestObject().put(OidcConfiguration.SCOPE, scope);
        authParams.url().put(OidcConfiguration.SCOPE, scope);
        val responseType = configContext.getResponseType();
        authParams.requestObject().put(OidcConfiguration.RESPONSE_TYPE, responseType);
        authParams.url().put(OidcConfiguration.RESPONSE_TYPE, responseType);
        authParams.requestObject().put(OidcConfiguration.RESPONSE_MODE, configContext.getResponseMode());
        authParams.requestObject().putAll(configContext.getCustomParams());
        val clientId = configContext.getConfiguration().getClientId();
        authParams.requestObject().put(CLIENT_ID, clientId);
        authParams.url().put(CLIENT_ID, clientId);
        val loginHint = configContext.getConfiguration().getLoginHint();
        if (loginHint != null && !loginHint.isEmpty()) {
            authParams.requestObject().put(OidcConfiguration.LOGIN_HINT, loginHint);
        }

        return authParams;
    }

    protected void addStateAndNonceParameters(final CallContext ctx, final Params params) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();
        val config = client.getConfiguration();

        // Init state for CSRF mitigation
        if (config.isWithState()) {
            val state = new State(config.getStateGenerator().generateValue(ctx));
            params.requestObject().put(OidcConfiguration.STATE, state.getValue());
            sessionStore.set(webContext, client.getStateSessionAttributeName(), state);
        } else {
            ANNOUNCE_WITH_STATE_DISABLED.announce();
        }

        // Init nonce for replay attack mitigation
        if (config.isUseNonce()) {
            val nonce = new Nonce();
            params.requestObject().put(OidcConfiguration.NONCE, nonce.getValue());
            sessionStore.set(webContext, client.getNonceSessionAttributeName(), nonce.getValue());
        }

        var pkceMethod = config.findPkceMethod();
        if (pkceMethod != null) {
            val verifier = new CodeVerifier(config.getCodeVerifierGenerator().generateValue(ctx));
            sessionStore.set(webContext, client.getCodeVerifierSessionAttributeName(), verifier);
            params.requestObject().put(OidcConfiguration.CODE_CHALLENGE, CodeChallenge.compute(pkceMethod, verifier).getValue());
            params.requestObject().put(OidcConfiguration.CODE_CHALLENGE_METHOD, pkceMethod.getValue());
        }
    }

    protected String buildAuthenticationRequestUrl(final Map<String, String> params) {
        final Map<String, String> newParams;

        val config = client.getConfiguration();
        if (config.isPushedAuthorizationRequest()) {
            val parUrl = config.getOpMetadataResolver().load().getPushedAuthorizationRequestEndpointURI();
            if (parUrl == null) {
                throw new OidcException("Pushed authorization request URL is undefined");
            }
            val multiParams = new HashMap<String, List<String>>();
            for (val entry: params.entrySet()) {
                val key = entry.getKey();
                val value = entry.getValue();
                if (value != null) {
                    multiParams.put(key, List.of(value));
                }
            }
            try {
                LOGGER.debug("Sending PAR request to: {}", parUrl);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.debug("PAR parameters: {}", multiParams.entrySet());
                } else {
                    LOGGER.debug("PAR parameter names: {}", multiParams.keySet());
                }
                val authzRequest = AuthorizationRequest.parse(multiParams);
                val clientAuth = config.getOpMetadataResolver().getClientAuthenticationPAREndpoint();
                val parRequest = new PushedAuthorizationRequest(
                    parUrl,
                    clientAuth,
                    authzRequest
                );
                val request = parRequest.toHTTPRequest();
                config.configureHttpRequest(request);
                val response = request.send();
                val parResponse = PushedAuthorizationResponse.parse(response);

                if (parResponse.indicatesSuccess()) {
                    val successResponse = parResponse.toSuccessResponse();
                    val requestUri = successResponse.getRequestURI();
                    LOGGER.debug("Received PAR response: {}", requestUri);

                    newParams = new HashMap<>();
                    newParams.put(CLIENT_ID, params.get(CLIENT_ID));
                    newParams.put("request_uri", requestUri.toString());

                } else {
                    val errorResponse = parResponse.toErrorResponse();
                    throw new OidcException(errorResponse.getErrorObject().getDescription());
                }
            } catch (final IOException | ParseException e) {
                throw new OidcException("Cannot send PAR request", e);
            }
        } else {
            newParams = params;
        }

        // Build authentication request query string
        String queryString;
        try {
            val urlParams = newParams.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));
            queryString = AuthenticationRequest.parse(urlParams).toQueryString();
            queryString = queryString.replaceAll("\\+", "%20");
        } catch (final Exception e) {
            throw new OidcException(e);
        }
        return config.getOpMetadataResolver().load().getAuthorizationEndpointURI().toString() + '?' + queryString;
    }
}

