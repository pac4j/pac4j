/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.scribe.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

/**
 * This class represents a specific OAuth service for Google using OAuth 2.0 protocol and WordPress. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class ExtendedOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {
    
    public ExtendedOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final String proxyHost,
                                      final int proxyPort) {
        super(api, config, proxyHost, proxyPort);
    }
    
    @Override
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(),
                                                           this.api.getAccessTokenEndpoint(), this.proxyHost,
                                                           this.proxyPort);
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
}
