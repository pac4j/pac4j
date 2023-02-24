package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;

/**
 * This class represents the OAuth API implementation for WordPress. It could be part of the Scribe library.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class WordPressApi20 extends DefaultApi20 {
    private static final String BASE_URL = "https://public-api.wordpress.com/oauth2/";

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return BASE_URL + "token";
    }

    /** {@inheritDoc} */
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return BASE_URL + "authorize";
    }
}
