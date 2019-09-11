package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * The OAuh 1.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10Configuration extends OAuthConfiguration<OAuth10aService, OAuth1AccessToken> {

    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String REQUEST_TOKEN = "requestToken";

    private DefaultApi10a api;

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("api", api);
        super.internalInit();
    }

    /**
     * Build an OAuth service from the web context.
     *
     * @param context the web context
     * @param client the client
     * @return the OAuth service
     */
    public OAuth10aService buildService(final WebContext context, final IndirectClient client) {
        init();

        final String finalCallbackUrl = client.computeFinalCallbackUrl(context);

        return api.createService(this.key, this.secret, finalCallbackUrl, this.scope, null, null, this.httpClientConfig, null);
    }

    /**
     * Return the name of the attribute storing in session the request token.
     *
     * @param clientName the client name
     * @return the name of the attribute storing in session the request token
     */
    public String getRequestTokenSessionAttributeName(final String clientName) {
        return clientName + "#" + REQUEST_TOKEN;
    }

    public DefaultApi10a getApi() {
        return api;
    }

    public void setApi(final DefaultApi10a api) {
        this.api = api;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "key", key, "secret", "[protected]", "tokenAsHeader", tokenAsHeader,
            "responseType", responseType, "scope", scope, "api", api, "hasBeenCancelledFactory", hasBeenCancelledFactory,
            "profileDefinition", profileDefinition, "httpClientConfig", httpClientConfig);
    }
}
