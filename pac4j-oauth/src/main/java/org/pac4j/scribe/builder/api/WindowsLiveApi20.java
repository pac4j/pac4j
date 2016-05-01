package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.utils.Preconditions;

/**
 * Fix url endpoints (waiting for the next Scribe release).
 *
 * @author Jerome Leleu
 * @since 1.6.0
 */
public class WindowsLiveApi20 extends DefaultApi20 {
    
    private static final String AUTHORIZE_URL = "https://login.live.com/oauth20_authorize.srf?client_id=%s&redirect_uri=%s&response_type=code";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";
    
    @Override
    public String getAccessTokenEndpoint() {
        return "https://login.live.com/oauth20_token.srf";
    }
    
    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(),
                                    "Must provide a valid url as callback. Live does not support OOB");
        
        // Append scope if present
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
                                 OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
        }
    }

}
