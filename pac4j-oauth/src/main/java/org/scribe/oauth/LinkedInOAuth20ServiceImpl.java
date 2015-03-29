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

import org.scribe.builder.api.StateApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;

/**
 * This service is dedicated for LinkedIn service using OAuth protocol version 2.0.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class LinkedInOAuth20ServiceImpl extends StateOAuth20ServiceImpl {
    
    public LinkedInOAuth20ServiceImpl(final StateApi20 api, final OAuthConfig config, final int connectTimeout,
                                      final int readTimeout, final String proxyHost, final int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }
    
    @Override
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter("oauth2_access_token", accessToken.getToken());
    }
}
