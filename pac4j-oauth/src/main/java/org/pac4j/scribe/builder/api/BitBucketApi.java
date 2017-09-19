package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

/**
 * This class represents the OAuth API implementation for Bitbucket.
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitBucketApi extends DefaultApi10a {

    private static final String OAUTH_ENDPOINT = "https://bitbucket.org/api/1.0/oauth/";

    @Override
    public String getAccessTokenEndpoint() {
        return OAUTH_ENDPOINT + "access_token";
    }

    @Override
    public String getAuthorizationUrl(final OAuth1RequestToken requestToken) {
        return OAUTH_ENDPOINT + "authenticate?oauth_token=" + requestToken.getToken();
    }

    @Override
    public String getRequestTokenEndpoint() {
        return OAUTH_ENDPOINT + "request_token";
    }
}
