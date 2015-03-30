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
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;

/**
 * This service is dedicated for Foursquare service using OAuth protocol version 2.0.
 * 
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {

    public FoursquareOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final int connectTimeout,
                                      final int readTimeout, final String proxyHost, final int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }

    @Override
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter("oauth_token", accessToken.getToken());
    }
}
