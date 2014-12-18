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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.oauth.profile.strava.StravaAccessToken;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

import java.io.IOException;

/**
 * This class represents the OAuth API implementation for Strava.
 *
 * @author Adrian Papusoi
 */
public final class StravaApi extends DefaultApi20 {

    /**
     * Strava authorization URL
     */
    private static final String AUTHORIZE_URL = "https://www.strava.com/oauth/authorize?approval_prompt=%s&response_type=code&client_id=%s&redirect_uri=%s";

    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

    private static final String ACCESS_TOKEN_URL = "https://www.strava.com/oauth/token";

    /**
     * Need to redefine the token extractor, because the token comes from Strava in json format.
     */
    private static final AccessTokenExtractor ACCESS_TOKEN_EXTRACTOR = new AccessTokenExtractor() {

        private ObjectMapper objectMapper = new ObjectMapper();

        public Token extract(String response) {
            Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                StravaAccessToken stravaAccessToken = objectMapper.readValue(response, StravaAccessToken.class);
                if (stravaAccessToken == null || stravaAccessToken.getAccessToken() == null) {
                    throw new OAuthException("Response body is incorrect. Can't extract a token from this: '" + response + "'", null);
                }
                String accessTokenValue = stravaAccessToken.getAccessToken();
                return new Token(accessTokenValue, "", response);
            } catch (IOException e) {
                throw new OAuthException("Response body is incorrect. Can't extract a token from this: '" + response + "'", null);
            }
        }
    };


    /**
     * possible values: auto or force. If force, the authorisation dialog is allways displayed by Strava.
     */
    private String approvalPrompt;

    public StravaApi(String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_URL;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return ACCESS_TOKEN_EXTRACTOR;
    }

    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(),
                "Must provide a valid url as callback. GitHub does not support OOB");

        // Append scope if present
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL, this.approvalPrompt, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
                    OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL, this.approvalPrompt, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
        }
    }

}
