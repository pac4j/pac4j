package org.pac4j.oauth.config;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.oauth.OAuth10aService;

/**
 * The OAuh 1.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10Configuration extends OAuthConfiguration<OAuth10aService, OAuth1AccessToken> {

    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String REQUEST_TOKEN = "requestToken";

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
