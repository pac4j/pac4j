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
 * This class represents a specific OAuth service for Google using OAuth 2.0 protocol and WordPress. It should be implemented natively in
 * Scribe in further release.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class ExtendedOAuthService20 implements OAuthService {
    private static final String VERSION = "2.0";
    
    private final DefaultApi20 api;
    private final OAuthConfig config;
    
    /**
     * Default constructor
     * 
     * @param api OAuth2.0 api information
     * @param config OAuth 2.0 configuration param object
     */
    public ExtendedOAuthService20(final DefaultApi20 api, final OAuthConfig config) {
        this.api = api;
        this.config = config;
    }
    
    /**
     * {@inheritDoc}
     */
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        // PATCH : body parameters instead of request parameters
        request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
        request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
        request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
        request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
        if (config.hasScope())
            request.addBodyParameter(OAuthConstants.SCOPE, config.getScope());
        // PATCH : + grant_type parameter
        request.addBodyParameter("grant_type", "authorization_code");
        final Response response = request.send();
        return api.getAccessTokenExtractor().extract(response.getBody());
    }
    
    /**
     * {@inheritDoc}
     */
    public Token getRequestToken() {
        throw new UnsupportedOperationException(
                                                "Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");
    }
    
    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return VERSION;
    }
    
    /**
     * {@inheritDoc}
     */
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAuthorizationUrl(final Token requestToken) {
        return api.getAuthorizationUrl(config);
    }
    
}
