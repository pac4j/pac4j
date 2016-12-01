package org.pac4j.oauth.config;

import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.oauth.client.OAuth10Client;

/**
 * The OAuh 1.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10Configuration extends OAuthConfiguration<OAuth10Client, OAuth10aService, OAuth1Token> {

    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String REQUEST_TOKEN = "requestToken";

    /**
     * Return the name of the attribute storing in session the request token.
     *
     * @return the name of the attribute storing in session the request token
     */
    public String getRequestTokenSessionAttributeName() {
        return getClient().getName() + "#" + REQUEST_TOKEN;
    }
}
