/*
  Copyright 2012 - 2014 Jerome Leleu

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

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.OrcidJsonExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

/**
 * This class represents the OAuth API implementation for ORCiD using OAuth protocol version 2.
 *
 * @author Jens Tinglev
 * @since 1.5.0
 */
public class OrcidApi20 extends DefaultApi20 {

    // TODO: Change this production server URLs
    private static final String AUTH_URL = "http://sandbox.orcid.org/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.sandbox.orcid.org/oauth/token";

    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_URL;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig oAuthConfig) {
        return String.format(AUTH_URL + "?client_id=%s&scope=%s&response_type=%s&redirect_uri=%s",
            oAuthConfig.getApiKey(), OAuthEncoder.encode(oAuthConfig.getScope()), "code", OAuthEncoder.encode(oAuthConfig.getCallback()));
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new OrcidJsonExtractor();
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

}
