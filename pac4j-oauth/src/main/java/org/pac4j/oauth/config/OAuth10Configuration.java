package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.oauth.OAuthService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;

/**
 * The OAuh 1.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@With
@AllArgsConstructor
@NoArgsConstructor
public class OAuth10Configuration extends OAuthConfiguration {

    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String REQUEST_TOKEN = "requestToken";

    /**
     * Build an OAuth service from the web context.
     *
     * @param context the web context
     * @param client the client
     * @return the OAuth service
     */
    @Override
    public OAuthService buildService(final WebContext context, final IndirectClient client) {
        init();

        val finalCallbackUrl = client.computeFinalCallbackUrl(context);

        return ((DefaultApi10a) api)
            .createService(this.key, this.secret, finalCallbackUrl, this.scope, null, null, this.httpClientConfig, null);
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
}
