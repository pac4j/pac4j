package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
import java.util.Map;

/**
 * This class represents the OAuth API implementation for DropBox using OAuth protocol version 2.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public class DropboxApi20 extends DefaultApi20 {

    public final static DropboxApi20 INSTANCE  = new DropboxApi20();

    private static final String AUTH_URL = "https://www.dropbox.com/1/oauth2/authorize";
    private static final String TOKEN_URL = "https://www.dropbox.com/1/oauth2/token";

    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_URL;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig oAuthConfig, Map<String, String> additionalParams) {
        return String.format(AUTH_URL + "?client_id=%s&response_type=code&redirect_uri=%s", oAuthConfig.getApiKey(), OAuthEncoder.encode(oAuthConfig.getCallback()));
    }
    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTH_URL;
    }  
    
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }
}
