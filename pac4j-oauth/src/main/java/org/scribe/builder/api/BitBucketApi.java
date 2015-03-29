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
package org.scribe.builder.api;

import org.scribe.model.Token;

/**
 * This class represents the OAuth API implementation for Bitbucket.
 * 
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitBucketApi extends DefaultApi10a {
  
    private static final String OAUTH_ENDPOINT = "https://bitbucket.org/api/1.0/oauth/";

    @Override
    public String getAccessTokenEndpoint() {
        return OAUTH_ENDPOINT + "access_token";
    }

    @Override
    public String getAuthorizationUrl(Token oauthToken) {
        return OAUTH_ENDPOINT + "authenticate?oauth_token=" + oauthToken.getToken();
    }

    @Override
    public String getRequestTokenEndpoint() {
        return OAUTH_ENDPOINT + "request_token";
    }
  
}
