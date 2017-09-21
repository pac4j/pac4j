package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.UrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.function.Function;

/**
 * The base OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuthConfiguration<S extends OAuthService<T>, T extends Token> extends InitializableWebObject {

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String RESPONSE_TYPE_CODE = "code";

    private String key;

    private String secret;

    private String clientName;

    private boolean tokenAsHeader;

    private String responseType = RESPONSE_TYPE_CODE;

    private String scope;

    private BaseApi<S> api;

    private Function<WebContext, Boolean> hasBeenCancelledFactory = ctx -> false;

    private OAuthProfileDefinition profileDefinition;

    private HttpClientConfig httpClientConfig;

    private String callbackUrl;

    private UrlResolver urlResolver;

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotBlank("clientName", this.clientName);
        CommonHelper.assertNotNull("api", api);
        CommonHelper.assertNotNull("hasBeenCancelledFactory", hasBeenCancelledFactory);
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("urlResolver", this.urlResolver);
    }

    /**
     * Build an OAuth service from the web context and with a state.
     *
     * @param context the web context
     * @param state a given state
     * @return the OAuth service
     */
    public S buildService(final WebContext context, final String state) {

        final String finalCallbackUrl = this.getUrlResolver().compute(this.getCallbackUrl(), context);

        final OAuthConfig oAuthConfig = new OAuthConfig(this.key, this.secret, finalCallbackUrl, this.scope,
            null, state, this.responseType, null, this.httpClientConfig, null);

        return getApi().createService(oAuthConfig);
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public boolean isTokenAsHeader() {
        return tokenAsHeader;
    }

    public void setTokenAsHeader(final boolean tokenAsHeader) {
        this.tokenAsHeader = tokenAsHeader;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(final String responseType) {
        this.responseType = responseType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public BaseApi<S> getApi() {
        return api;
    }

    public void setApi(final BaseApi<S> api) {
        this.api = api;
    }

    public Function<WebContext, Boolean> getHasBeenCancelledFactory() {
        return hasBeenCancelledFactory;
    }

    public void setHasBeenCancelledFactory(final Function<WebContext, Boolean> hasBeenCancelledFactory) {
        this.hasBeenCancelledFactory = hasBeenCancelledFactory;
    }

    public OAuthProfileDefinition getProfileDefinition() {
        return profileDefinition;
    }

    public void setProfileDefinition(final OAuthProfileDefinition profileDefinition) {
        this.profileDefinition = profileDefinition;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    public HttpClientConfig getHttpClientConfig() {
        return httpClientConfig;
    }

    public void setHttpClientConfig(final HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "key", key, "secret", "[protected]", "clientName", clientName,
            "tokenAsHeader", tokenAsHeader, "responseType", responseType, "scope", scope, "api", api,
                "hasBeenCancelledFactory", hasBeenCancelledFactory, "profileDefinition", profileDefinition,
            "httpClientConfig", httpClientConfig, "callbackUrl", callbackUrl, "urlResolver", urlResolver);
    }
}
