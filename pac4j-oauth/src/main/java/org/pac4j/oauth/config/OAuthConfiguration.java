package org.pac4j.oauth.config;

import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

/**
 * The base OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class OAuthConfiguration extends BaseClientConfiguration {

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String RESPONSE_TYPE_CODE = "code";

    protected String key;

    protected String secret;

    protected boolean tokenAsHeader;

    protected String responseType = RESPONSE_TYPE_CODE;

    protected String scope;

    protected HasBeenCancelledFactory hasBeenCancelledFactory = ctx -> false;

    protected OAuthProfileDefinition profileDefinition;

    protected HttpClientConfig httpClientConfig;

    protected Object api;

    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("api", api);
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotNull("hasBeenCancelledFactory", hasBeenCancelledFactory);
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
    }

    public abstract OAuthService buildService(final WebContext context, final IndirectClient client);

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

    public HasBeenCancelledFactory getHasBeenCancelledFactory() {
        return hasBeenCancelledFactory;
    }

    public void setHasBeenCancelledFactory(final HasBeenCancelledFactory hasBeenCancelledFactory) {
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

    public Object getApi() {
        return api;
    }

    public void setApi(final Object api) {
        this.api = api;
    }
}
