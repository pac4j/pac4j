package org.pac4j.oauth.config;

import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.oauth.OAuthService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.http.interceptor.HttpInterceptor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.http.InterceptingHttpClient;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.oauth.util.OAuthProtocolMessages;

/**
 * The base OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
@Setter
@ToString(exclude = "secret")
@Accessors(chain = true)
public abstract class OAuthConfiguration extends BaseClientConfiguration {

    /** Constant <code>OAUTH_TOKEN="oauth_token"</code> */
    public static final String OAUTH_TOKEN = "oauth_token";

    /** Constant <code>RESPONSE_TYPE_CODE="code"</code> */
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

    /** The HTTP client ScribeJava must use, instead of the one it builds from the HTTP client configuration; none by default. */
    protected HttpClient httpClient;

    /** Applied to every request sent to the OAuth server (an {@code OAuthHttpRequest}), once ScribeJava has built it; none by default. */
    protected HttpInterceptor httpInterceptor;

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("api", api);
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotNull("hasBeenCancelledFactory", hasBeenCancelledFactory);
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
    }

    /**
     * <p>buildService.</p>
     *
     * @param context the call context
     * @param client a {@link IndirectClient} object
     * @return a {@link OAuthService} object
     */
    public abstract OAuthService buildService(final CallContext context, final IndirectClient client);

    /**
     * Build the HTTP client ScribeJava must use: the defined one, or the one of the HTTP client configuration, logging
     * the messages when the logger is enabled and applying the HTTP interceptor when there is one. None, for
     * ScribeJava to build its own, when nothing of that is needed.
     *
     * @param context the call context
     * @return the HTTP client, or null
     */
    protected HttpClient buildHttpClient(final CallContext context) {
        if (httpClient == null && httpInterceptor == null) {
            return OAuthProtocolMessages.httpClient(httpClientConfig);
        }
        val client = httpClient == null ? OAuthProtocolMessages.requiredHttpClient(httpClientConfig)
            : OAuthProtocolMessages.LOGGER.isEnabled() ? OAuthProtocolMessages.httpClient(httpClient) : httpClient;
        return httpInterceptor == null ? client : new InterceptingHttpClient(client, httpInterceptor, context);
    }
}
