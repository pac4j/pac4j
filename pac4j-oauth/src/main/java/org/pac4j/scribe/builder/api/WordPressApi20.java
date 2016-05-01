package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.utils.Preconditions;

/**
 * This class represents the OAuth API implementation for WordPress. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class WordPressApi20 extends DefaultApi20 {
    private static final String BASE_URL = "https://public-api.wordpress.com/oauth2/";
    
    private static final String AUTHORIZE_URL = BASE_URL + "authorize?client_id=%s&redirect_uri=%s&response_type=code";
    
    @Override
    public String getAccessTokenEndpoint() {
        return BASE_URL + "token";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(),
                                    "Must provide a valid url as callback. WordPress does not support OOB");
        
        return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

}
