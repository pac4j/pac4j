package org.pac4j.oidc.redirect;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.config.OidcConfigurationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class OidcRedirectionActionBuilder implements RedirectionActionBuilder {
    private static final Logger logger = LoggerFactory.getLogger(OidcRedirectionActionBuilder.class);

    protected OidcClient client;

    public OidcRedirectionActionBuilder(final OidcClient client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext context, final SessionStore sessionStore) {
        final var configContext = new OidcConfigurationContext(context, client.getConfiguration());
        final var params = buildParams(context);

        final var computedCallbackUrl = client.computeFinalCallbackUrl(context);
        params.put(OidcConfiguration.REDIRECT_URI, computedCallbackUrl);

        addStateAndNonceParameters(context, sessionStore, params);

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

        final var location = buildAuthenticationRequestUrl(params);
        logger.debug("Authentication request url: {}", location);

        return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, location));
    }

    protected Map<String, String> buildParams(final WebContext webContext) {
        final var configContext = new OidcConfigurationContext(webContext, client.getConfiguration());

        final var authParams = new HashMap<String, String>();
        authParams.put(OidcConfiguration.SCOPE, configContext.getScope());
        authParams.put(OidcConfiguration.RESPONSE_TYPE, configContext.getResponseType());
        authParams.put(OidcConfiguration.RESPONSE_MODE, configContext.getResponseMode());
        authParams.putAll(configContext.getCustomParams());
        authParams.put(OidcConfiguration.CLIENT_ID, configContext.getConfiguration().getClientId());

        return new HashMap<>(authParams);
    }

    protected void addStateAndNonceParameters(final WebContext context, final SessionStore sessionStore, final Map<String, String> params) {
        // Init state for CSRF mitigation
        if (client.getConfiguration().isWithState()) {
            final var state = new State(client.getConfiguration().getStateGenerator().generateValue(context, sessionStore));
            params.put(OidcConfiguration.STATE, state.getValue());
            sessionStore.set(context, client.getStateSessionAttributeName(), state);
        }

        // Init nonce for replay attack mitigation
        if (client.getConfiguration().isUseNonce()) {
            final var nonce = new Nonce();
            params.put(OidcConfiguration.NONCE, nonce.getValue());
            sessionStore.set(context, client.getNonceSessionAttributeName(), nonce.getValue());
        }

        var pkceMethod = client.getConfiguration().findPkceMethod();
        if (pkceMethod != null) {
            final var verfifier = new CodeVerifier(
                client.getConfiguration().getCodeVerifierGenerator().generateValue(context, sessionStore));
            sessionStore.set(context, client.getCodeVerifierSessionAttributeName(), verfifier);
            params.put(OidcConfiguration.CODE_CHALLENGE, CodeChallenge.compute(pkceMethod, verfifier).getValue());
            params.put(OidcConfiguration.CODE_CHALLENGE_METHOD, pkceMethod.getValue());
        }
    }

    protected String buildAuthenticationRequestUrl(final Map<String, String> params) {
        // Build authentication request query string
        final String queryString;
        try {
            queryString = AuthenticationRequest.parse(params.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())))).toQueryString();
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
        return client.getConfiguration().getProviderMetadata().getAuthorizationEndpointURI().toString() + '?' + queryString;
    }
}
