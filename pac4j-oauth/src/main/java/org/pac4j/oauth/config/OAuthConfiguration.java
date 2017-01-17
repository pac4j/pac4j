package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.SignatureType;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
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
public class OAuthConfiguration<C extends IndirectClient, S extends OAuthService<?>, T extends Token> extends InitializableWebObject {

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String RESPONSE_TYPE_CODE = "code";

    private C client;

    private String key;

    private String secret;

    private boolean tokenAsHeader;

    private int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    private String responseType = RESPONSE_TYPE_CODE;

    private String scope;

    private BaseApi<S> api;

    private boolean hasGrantType;

    private Function<WebContext, Boolean> hasBeenCancelledFactory = ctx -> false;

    private OAuthProfileDefinition profileDefinition;

    protected S service;

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("client", this.client);
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotNull("api", api);
        CommonHelper.assertNotNull("hasBeenCancelledFactory", hasBeenCancelledFactory);
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);

        this.service = buildService(context, null);
    }

    /**
     * Build an OAuth service from the web context and with a state.
     *
     * @param context the web context
     * @param state a given state
     * @return the OAuth service
     */
    public S buildService(final WebContext context, final String state) {
        return getApi().createService(buildOAuthConfig(context, state));
    }

    protected OAuthConfig buildOAuthConfig(final WebContext context, final String state) {

        final String finalCallbackUrl = this.client.getCallbackUrlResolver().compute(this.client.getCallbackUrl(), context);

        return new OAuthConfig(this.key, this.secret, finalCallbackUrl, SignatureType.Header, this.scope,
                null, state, this.responseType, null, this.connectTimeout, this.readTimeout,
                null, null);
    }

    public S getService() {
        return this.service;
    }

    public C getClient() {
        return client;
    }

    public void setClient(final C client) {
        this.client = client;
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

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
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

    public boolean isHasGrantType() {
        return hasGrantType;
    }

    public void setHasGrantType(final boolean hasGrantType) {
        this.hasGrantType = hasGrantType;
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

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "key", key, "secret", secret, "tokenAsHeader", tokenAsHeader,
                "connectTimeout", connectTimeout, "readTimeout", readTimeout, "responseType", responseType,
                "scope", scope, "api", api, "hasGrantType", hasGrantType, "service", service,
                "hasBeenCancelledFactory", hasBeenCancelledFactory, "profileDefinition", profileDefinition);
    }
}
