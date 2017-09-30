package org.pac4j.oidc.redirect;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Redirect to the OpenID Connect provider.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcRedirectActionBuilder extends InitializableObject implements RedirectActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OidcRedirectActionBuilder.class);

    protected OidcConfiguration configuration;

    private String clientName;

    private String callbackUrl;

    private CallbackUrlResolver callbackUrlResolver;

    private Map<String, String> authParams;

    public OidcRedirectActionBuilder() {}

    public OidcRedirectActionBuilder(final OidcConfiguration configuration, final String clientName,
                                     final String callbackUrl, final CallbackUrlResolver callbackUrlResolver) {
        this.configuration = configuration;
        this.clientName = clientName;
        this.callbackUrl = callbackUrl;
        this.callbackUrlResolver = callbackUrlResolver;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotBlank("clientName", clientName);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotNull("callbackUrlResolver", callbackUrlResolver);

        configuration.init();

        this.authParams = new HashMap<>();
        // add scope
        final String scope = configuration.getScope();
        if(CommonHelper.isNotBlank(scope)){
            this.authParams.put(OidcConfiguration.SCOPE, scope);
        } else {
            // default values
            this.authParams.put(OidcConfiguration.SCOPE, "openid profile email");
        }
        // add response type
        final String responseType = configuration.getResponseType();
        if (CommonHelper.isNotBlank(responseType)) {
            this.authParams.put(OidcConfiguration.RESPONSE_TYPE, responseType);
        } else {
            this.authParams.put(OidcConfiguration.RESPONSE_TYPE, "code");
        }
        // add response mode?
        final String responseMode = configuration.getResponseMode();
        if (CommonHelper.isNotBlank(responseMode)) {
            this.authParams.put(OidcConfiguration.RESPONSE_MODE, responseMode);
        }

        // add custom values
        this.authParams.putAll(configuration.getCustomParams());
        // client id
        this.authParams.put(OidcConfiguration.CLIENT_ID, configuration.getClientId());
    }

    @Override
    public RedirectAction redirect(final WebContext context) {
        init();

        final Map<String, String> params = buildParams();
        final String computedCallbackUrl = callbackUrlResolver.compute(callbackUrl, clientName, context);
        params.put(OidcConfiguration.REDIRECT_URI, computedCallbackUrl);

        addStateAndNonceParameters(context, params);

        final String location = buildAuthenticationRequestUrl(params);
        logger.debug("Authentication request url: {}", location);

        return RedirectAction.redirect(location);
    }

    protected Map<String, String> buildParams() {
        return new HashMap<>(this.authParams);
    }

    protected void addStateAndNonceParameters(final WebContext context, final Map<String, String> params) {
        // Init state for CSRF mitigation
        State state = new State();
        params.put(OidcConfiguration.STATE, state.getValue());
        context.getSessionStore().set(context, OidcConfiguration.STATE_SESSION_ATTRIBUTE, state);
        // Init nonce for replay attack mitigation
        if (configuration.isUseNonce()) {
            Nonce nonce = new Nonce();
            params.put(OidcConfiguration.NONCE, nonce.getValue());
            context.getSessionStore().set(context, OidcConfiguration.NONCE_SESSION_ATTRIBUTE, nonce.getValue());
        }
    }

    protected String buildAuthenticationRequestUrl(final Map<String, String> params) {
        // Build authentication request query string
        final String queryString;
        try {
            queryString = AuthenticationRequest.parse(params).toQueryString();
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
        return configuration.getProviderMetadata().getAuthorizationEndpointURI().toString() + "?" + queryString;
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    public CallbackUrlResolver getCallbackUrlResolver() {
        return callbackUrlResolver;
    }

    public void setCallbackUrlResolver(final CallbackUrlResolver callbackUrlResolver) {
        this.callbackUrlResolver = callbackUrlResolver;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "clientName", clientName,
            "callbackUrl", callbackUrl, "callbackUrlResolver", callbackUrlResolver);
    }
}
