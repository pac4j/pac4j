package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.model.OAuthConfig;
import java.util.Map;
import java.util.Map.Entry;

/**
 * OAuth API class for the GenericOAuth20Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericApi20 extends DefaultApi20 {

    private final static String AUTHORIZATION_URL = "%s?response_type=code&client_id=%s&redirect_uri=%s";

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
    public String getAuthorizationUrl(final OAuthConfig config, Map<String, String> additionalParams) {
        
        StringBuilder url = new StringBuilder(String.format(AUTHORIZATION_URL, authUrl, config.getApiKey(), 
            OAuthEncoder.encode(config.getCallback())));
                
        if (config.getScope() != null) {
            url.append("&scope=").append(OAuthEncoder.encode(config.getScope()));            
        }
        
        if (config.getState() != null) {
            url.append("&state=").append(OAuthEncoder.encode(config.getState()));
        }
        
        if (additionalParams != null && !additionalParams.isEmpty()) {
            for (Entry entry: additionalParams.entrySet()) {
                if (entry.getValue() != null) {
                    url.append("&").append(entry.getKey()).append("=").append(OAuthEncoder.encode(entry.getValue().toString()));
                }
            }
        }
        
        return url.toString();
    }
    
    @Override
    protected String getAuthorizationBaseUrl() {
        return authUrl;
    }      
}
