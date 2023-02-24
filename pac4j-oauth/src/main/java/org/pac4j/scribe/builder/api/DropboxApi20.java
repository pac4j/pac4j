package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;

/**
 * This class represents the OAuth API implementation for DropBox using OAuth protocol version 2.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public class DropboxApi20 extends DefaultApi20 {

    /** Constant <code>INSTANCE</code> */
    public final static DropboxApi20 INSTANCE  = new DropboxApi20();

    private static final String AUTH_URL = "https://www.dropbox.com/oauth2/authorize";
    private static final String TOKEN_URL = "https://api.dropboxapi.com/oauth2/token";

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_URL;
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTH_URL;
    }

    /** {@inheritDoc} */
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }
}
