package org.pac4j.oidc.redirect;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.config.OidcConfigurationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Redirect to the OpenID Connect provider.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@SuppressWarnings("unchecked")
@Slf4j
public class OidcRedirectionActionBuilder implements RedirectionActionBuilder {

    protected OidcClient client;

    public OidcRedirectionActionBuilder(final OidcClient client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val webContext = ctx.webContext();

        val configContext = new OidcConfigurationContext(webContext, client.getConfiguration());
        val params = buildParams(webContext);

        val computedCallbackUrl = client.computeFinalCallbackUrl(webContext);
        params.put(OidcConfiguration.REDIRECT_URI, computedCallbackUrl);

        addStateAndNonceParameters(ctx, params);

        var maxAge = configContext.getMaxAge();
        if (maxAge != null) {
            params.put(OidcConfiguration.MAX_AGE, maxAge.toString());
        }
        if (configContext.isForceAuthn()) {
            params.put(OidcConfiguration.PROMPT, "login");
            params.put(OidcConfiguration.MAX_AGE, "0");
        }
        if (configContext.isPassive()) {
            params.put(OidcConfiguration.PROMPT, "none");
        }

        val location = buildAuthenticationRequestUrl(params);
        LOGGER.debug("Authentication request url: {}", location);

        return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, location));
    }

    protected Map<String, String> buildParams(final WebContext webContext) {
        val configContext = new OidcConfigurationContext(webContext, client.getConfiguration());

        val authParams = new HashMap<String, String>();
        authParams.put(OidcConfiguration.SCOPE, configContext.getScope());
        authParams.put(OidcConfiguration.RESPONSE_TYPE, configContext.getResponseType());
        authParams.put(OidcConfiguration.RESPONSE_MODE, configContext.getResponseMode());
        authParams.putAll(configContext.getCustomParams());
        authParams.put(OidcConfiguration.CLIENT_ID, configContext.getConfiguration().getClientId());

        return new HashMap<>(authParams);
    }

    protected void addStateAndNonceParameters(final CallContext ctx, final Map<String, String> params) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        // Init state for CSRF mitigation
        if (client.getConfiguration().isWithState()) {
            val state = new State(client.getConfiguration().getStateGenerator().generateValue(ctx));
            params.put(OidcConfiguration.STATE, state.getValue());
            sessionStore.set(webContext, client.getStateSessionAttributeName(), state);
        }

        // Init nonce for replay attack mitigation
        if (client.getConfiguration().isUseNonce()) {
            val nonce = new Nonce();
            params.put(OidcConfiguration.NONCE, nonce.getValue());
            sessionStore.set(webContext, client.getNonceSessionAttributeName(), nonce.getValue());
        }

        var pkceMethod = client.getConfiguration().findPkceMethod();
        if (pkceMethod != null) {
            val verfifier = new CodeVerifier(
                client.getConfiguration().getCodeVerifierGenerator().generateValue(ctx));
            sessionStore.set(webContext, client.getCodeVerifierSessionAttributeName(), verfifier);
            params.put(OidcConfiguration.CODE_CHALLENGE, CodeChallenge.compute(pkceMethod, verfifier).getValue());
            params.put(OidcConfiguration.CODE_CHALLENGE_METHOD, pkceMethod.getValue());
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
            throw new TechnicalException(e);
        }
        return client.getConfiguration().getOpMetadataResolver().load().getAuthorizationEndpointURI().toString() + '?' + queryString;
    }
}
