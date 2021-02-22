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
public class OidcRedirectionActionBuilder implements RedirectionActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OidcRedirectionActionBuilder.class);

    protected OidcConfiguration configuration;

    protected OidcClient client;

    private Map<String, String> authParams;

    public OidcRedirectionActionBuilder(final OidcConfiguration configuration, final OidcClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;

        this.authParams = new HashMap<>();
        // add scope
        final var scope = configuration.getScope();
        if (CommonHelper.isNotBlank(scope)) {
            this.authParams.put(OidcConfiguration.SCOPE, scope);
        } else {
            // default values
            this.authParams.put(OidcConfiguration.SCOPE, "openid profile email");
        }
        // add response type
        this.authParams.put(OidcConfiguration.RESPONSE_TYPE, configuration.getResponseType());
        // add response mode?
        final var responseMode = configuration.getResponseMode();
        if (CommonHelper.isNotBlank(responseMode)) {
            this.authParams.put(OidcConfiguration.RESPONSE_MODE, responseMode);
        }

        // add custom values
        this.authParams.putAll(configuration.getCustomParams());
        // client id
        this.authParams.put(OidcConfiguration.CLIENT_ID, configuration.getClientId());
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext context, final SessionStore sessionStore) {
        final var params = buildParams();


        final var computedCallbackUrl = client.computeFinalCallbackUrl(context);
        params.put(OidcConfiguration.REDIRECT_URI, computedCallbackUrl);

        addStateAndNonceParameters(context, sessionStore, params);

        if (configuration.getMaxAge() != null) {
            params.put(OidcConfiguration.MAX_AGE, configuration.getMaxAge().toString());
        }
        context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN)
            .ifPresent(attr -> {
                params.put(OidcConfiguration.PROMPT, "login");
                params.put(OidcConfiguration.MAX_AGE, "0");
            });
        context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE)
            .ifPresent(attr -> {
                params.put(OidcConfiguration.PROMPT, "none");
            });

        final var location = buildAuthenticationRequestUrl(params);
        logger.debug("Authentication request url: {}", location);

        return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, location));
    }

    protected Map<String, String> buildParams() {
        return new HashMap<>(this.authParams);
    }

    protected void addStateAndNonceParameters(final WebContext context, final SessionStore sessionStore, final Map<String, String> params) {
        // Init state for CSRF mitigation
        if (configuration.isWithState()) {
            final var state = new State(configuration.getStateGenerator().generateValue(context, sessionStore));
            params.put(OidcConfiguration.STATE, state.getValue());
            sessionStore.set(context, client.getStateSessionAttributeName(), state);
        }

        // Init nonce for replay attack mitigation
        if (configuration.isUseNonce()) {
            final var nonce = new Nonce();
            params.put(OidcConfiguration.NONCE, nonce.getValue());
            sessionStore.set(context, client.getNonceSessionAttributeName(), nonce.getValue());
        }

        var pkceMethod = configuration.findPkceMethod();
        if (pkceMethod != null) {
            final var verfifier = new CodeVerifier(
                configuration.getCodeVerifierGenerator().generateValue(context, sessionStore));
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
        return configuration.getProviderMetadata().getAuthorizationEndpointURI().toString() + "?" + queryString;
    }
}
