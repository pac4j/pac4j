package org.scribe.up.addon_to_scribe;

import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthConfig;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * This class allow to add the Facebook state parameter to authorization URL through the overloaded method getAuthorizationUrl
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.0
 */
public class ExtendedFacebookApi extends FacebookApi {
    
    private static final String AUTHORIZE_URL_WITH_STATE = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s&state=%s";
    private static final String SCOPED_AUTHORIZE_URL_WITH_STATE = AUTHORIZE_URL_WITH_STATE + "&scope=%s";
    
    @Override
    public OAuthService createService(final OAuthConfig config) {
        return new FacebookOAuth20ServiceImpl(this, config);
    }
    
    public String getAuthorizationUrl(final OAuthConfig config, final String facebookState) {
        Preconditions.checkValidUrl(config.getCallback(),
                                    "Must provide a valid url as callback. Facebook does not support OOB");
        
        // Append scope if present
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                                 OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(facebookState),
                                 OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                                 OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(facebookState));
        }
    }
    
}
