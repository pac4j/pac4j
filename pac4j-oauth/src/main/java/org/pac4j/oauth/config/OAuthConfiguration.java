package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.function.Function;

/**
 * The base OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuthConfiguration<S extends OAuthService, T extends Token> extends InitializableObject {

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String RESPONSE_TYPE_CODE = "code";

    private String key;

    private String secret;

    private boolean tokenAsHeader;

    private String responseType = RESPONSE_TYPE_CODE;

    private String scope;

    private BaseApi<S> api;

    private Function<WebContext, Boolean> hasBeenCancelledFactory = ctx -> false;

    private OAuthProfileDefinition profileDefinition;

    private HttpClientConfig httpClientConfig;

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotNull("api", api);
        CommonHelper.assertNotNull("hasBeenCancelledFactory", hasBeenCancelledFactory);
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
    }

    /**
     * Build an OAuth service from the web context and with a state.
     *
     * @param context the web context
     * @param client the client
     * @return the OAuth service
     */
    public S buildService(final WebContext context, final IndirectClient client) {
        init();

        final String finalCallbackUrl = client.computeFinalCallbackUrl(context);

        return getApi().createService(this.key, this.secret, finalCallbackUrl, this.scope, null, this.responseType, null,
                this.httpClientConfig, null);
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

    public HttpClientConfig getHttpClientConfig() {
        return httpClientConfig;
    }

    public void setHttpClientConfig(final HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "key", key, "secret", "[protected]", "tokenAsHeader", tokenAsHeader,
            "responseType", responseType, "scope", scope, "api", api, "hasBeenCancelledFactory", hasBeenCancelledFactory,
            "profileDefinition", profileDefinition, "httpClientConfig", httpClientConfig);
    }
}
