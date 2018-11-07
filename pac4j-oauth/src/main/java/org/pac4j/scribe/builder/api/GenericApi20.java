package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;

/**
 * OAuth API class for the GenericOAuth20Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericApi20 extends DefaultApi20 {

    protected final String authUrl;
    protected final String tokenUrl;
    protected Verb accessTokenVerb = Verb.POST;

    public GenericApi20(final String authUrl, final String tokenUrl) {
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return accessTokenVerb;
    }
    
    public void setAccessTokenVerb(Verb verb) {
        accessTokenVerb = verb;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return tokenUrl;
    }
    
    @Override
    protected String getAuthorizationBaseUrl() {
        return authUrl;
    }      
}
