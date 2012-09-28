package org.scribe.up.addon_to_scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.oauth.OAuth20ServiceImpl;

/**
 * This class overload getAuthorizationUrl method to allow to add the Facebook state parameter to authorization URL
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.0
 */
public class FacebookOAuth20ServiceImpl extends OAuth20ServiceImpl {
    
    private final DefaultApi20 api;
    
    private final OAuthConfig config;
    
    public FacebookOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config) {
        super(api, config);
        this.api = api;
        this.config = config;
    }
    
    public String getAuthorizationUrl(final String facebookState) {
        return ((ExtendedFacebookApi) this.api).getAuthorizationUrl(this.config, facebookState);
    }
    
}
