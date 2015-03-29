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

/**
 * This class overload getAuthorizationUrl method to allow to add the state parameter to authorization url.
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.0
 */
public class StateOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl implements StateOAuth20Service {
    
    public StateOAuth20ServiceImpl(final StateApi20 api, final OAuthConfig config, final int connectTimeout,
                                   final int readTimeout, final String proxyHost, final int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }

    public StateOAuth20ServiceImpl(final StateApi20 api, final OAuthConfig config, final int connectTimeout,
                                   final int readTimeout, final String proxyHost, final int proxyPort,
                                   final boolean getParameter, final boolean addGrantType) {
    	super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort, getParameter, addGrantType);
    }

    @Override
    public String getAuthorizationUrl(final String state) {
        return ((StateApi20) this.api).getAuthorizationUrl(this.config, state);
    }
}
