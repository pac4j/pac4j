/*
  Copyright 2012 - 2015 pac4j organization

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
 * This class represents a specific OAuth service with proxy capabilities for OAuth 2.0 protocol. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class ProxyOAuth20ServiceImpl extends OAuth20ServiceImpl {
    
    protected final DefaultApi20 api;
    protected final OAuthConfig config;
    protected final int connectTimeout;
    protected final int readTimeout;
    protected final String proxyHost;
    protected final int proxyPort;
    protected final boolean getParameter;
    protected final boolean addGrantType;
    
    public ProxyOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final int connectTimeout,
                                   final int readTimeout, final String proxyHost, final int proxyPort) {
        this(api, config, connectTimeout, readTimeout, proxyHost, proxyPort, true, false);
    }
    
    public ProxyOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final int connectTimeout,
                                   final int readTimeout, final String proxyHost, final int proxyPort,
                                   final boolean getParameter, final boolean addGrantType) {
        super(api, config);
        this.api = api;
        this.config = config;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.getParameter = getParameter;
        this.addGrantType = addGrantType;
    }
    
    @Override
    public Token getAccessToken(final Token requestToken, final Verifier verifier) {
        final OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(),
                                                           this.api.getAccessTokenEndpoint(), this.connectTimeout,
                                                           this.readTimeout, this.proxyHost, this.proxyPort);
        if (this.getParameter) {
            request.addQuerystringParameter(OAuthConstants.CLIENT_ID, this.config.getApiKey());
            request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, this.config.getApiSecret());
            request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
            request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
            if (this.config.hasScope()) {
                request.addQuerystringParameter(OAuthConstants.SCOPE, this.config.getScope());
            }
            if (this.addGrantType) {
                request.addQuerystringParameter("grant_type", "authorization_code");
            }
        } else {
            request.addBodyParameter(OAuthConstants.CLIENT_ID, this.config.getApiKey());
            request.addBodyParameter(OAuthConstants.CLIENT_SECRET, this.config.getApiSecret());
            request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
            request.addBodyParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
            if (this.config.hasScope()) {
                request.addBodyParameter(OAuthConstants.SCOPE, this.config.getScope());
            }
            if (this.addGrantType) {
                request.addBodyParameter("grant_type", "authorization_code");
            }
        }
        final Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }
}
