package org.scribe.up.addon_to_scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * This class represents the OAuth API implementation for Google using OAuth protocol version 2. It should be implemented natively in Scribe
 * in further release.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class GoogleApi20 extends DefaultApi20 {
    private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/auth?client_id=%s&redirect_uri=%s&scope=%s&response_type=code";
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        return String.format(AUTHORIZATION_URL, config.getApiKey(), config.getCallback(), config.getScope());
    }
    
    @Override
    public String getAccessTokenEndpoint() {
        return "https://accounts.google.com/o/oauth2/token";
    }
    
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new Google2JsonExtractor();
    }
    
    @Override
    public OAuthService createService(final OAuthConfig config) {
        return new ExtendedOAuthService20(this, config);
    }
}
