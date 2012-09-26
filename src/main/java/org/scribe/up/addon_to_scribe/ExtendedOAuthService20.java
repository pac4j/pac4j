/*
  Copyright 2012 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
        final OAuthRequest request = new OAuthRequest(this.api.getAccessTokenVerb(), this.api.getAccessTokenEndpoint());
        // PATCH : body parameters instead of request parameters
        request.addBodyParameter(OAuthConstants.CLIENT_ID, this.config.getApiKey());
        request.addBodyParameter(OAuthConstants.CLIENT_SECRET, this.config.getApiSecret());
        request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
        request.addBodyParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
        if (this.config.hasScope())
            request.addBodyParameter(OAuthConstants.SCOPE, this.config.getScope());
        // PATCH : + grant_type parameter
        request.addBodyParameter("grant_type", "authorization_code");
        final Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
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
        return this.api.getAuthorizationUrl(this.config);
    }
    
}
