package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi10a;

/**
 * This class represents the OAuth API implementation for Bitbucket.
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitBucketApi extends DefaultApi10a {

    private static final String OAUTH_ENDPOINT = "https://bitbucket.org/api/1.0/oauth/";

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return OAUTH_ENDPOINT + "access_token";
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return OAUTH_ENDPOINT + "authenticate";
    }

    /** {@inheritDoc} */
    @Override
    public String getRequestTokenEndpoint() {
        return OAUTH_ENDPOINT + "request_token";
    }
}
