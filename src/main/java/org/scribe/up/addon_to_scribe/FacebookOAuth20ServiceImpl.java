package org.scribe.up.addon_to_scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * This class overload getAuthorizationUrl method to allow to add the Facebook state parameter to authorization URL
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.1
 */
public class FacebookOAuth20ServiceImpl implements OAuthService {
    
    private static final String VERSION = "2.0";
    
    private final DefaultApi20 api;
    
    private final OAuthConfig config;
    
    public FacebookOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config) {
        this.api = api;
        this.config = config;
    }
    
    public String getAuthorizationUrl(final Token requestToken) {
        return this.api.getAuthorizationUrl(this.config);
    }
    
    public String getAuthorizationUrl(final String facebookState) {
        return ((ExtendedFacebookApi) this.api).getAuthorizationUrl(this.config, facebookState);
    }
    
    public Token getRequestToken() {
        throw new UnsupportedOperationException(
                                                "Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");
    }
    
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new OAuthRequest(this.api.getAccessTokenVerb(), this.api.getAccessTokenEndpoint());
        request.addQuerystringParameter(OAuthConstants.CLIENT_ID, this.config.getApiKey());
        request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, this.config.getApiSecret());
        request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
        request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
        if (this.config.hasScope()) {
            request.addQuerystringParameter(OAuthConstants.SCOPE, this.config.getScope());
        }
        final Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }
    
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
    }
    
    public String getVersion() {
        return VERSION;
    }
    
}
