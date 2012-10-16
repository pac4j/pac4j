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
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;

/**
 * This class represents a specific OAuth service with proxy capabilities for OAuth 2.0 protocol. It should be implemented natively in
 * Scribe in further release.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class ProxyOAuth20ServiceImpl extends OAuth20ServiceImpl {
    
    protected final DefaultApi20 api;
    protected final OAuthConfig config;
    protected final String proxyHost;
    protected final int proxyPort;
    
    public ProxyOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final String proxyHost,
                                   final int proxyPort) {
        super(api, config);
        this.api = api;
        this.config = config;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }
    
    @Override
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(),
                                                           this.api.getAccessTokenEndpoint(), this.proxyHost,
                                                           this.proxyPort);
        request.addQuerystringParameter(OAuthConstants.CLIENT_ID, this.config.getApiKey());
        request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, this.config.getApiSecret());
        request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
        request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
        if (this.config.hasScope())
            request.addQuerystringParameter(OAuthConstants.SCOPE, this.config.getScope());
        final Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }
}
