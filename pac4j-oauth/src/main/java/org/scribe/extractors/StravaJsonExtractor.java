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
package org.scribe.extractors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

import java.io.IOException;

/**
 * Json token extractor for Strava using Jackson to parse the response.
 *
 * @author Adrian Papusoi
 */
public class StravaJsonExtractor implements AccessTokenExtractor {

    private static final String OAUTH_EXCEPTION_INVALID_TOKEN_MESSAGE = "Response body is incorrect. Can't extract a token from this: '";

    /**
     * Object mapper needed to extract the token from the Strava response.
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    public StravaJsonExtractor() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");

        try {
            StravaJsonExtractor.StravaAccessToken stravaAccessToken = objectMapper.readValue(response, StravaJsonExtractor.StravaAccessToken.class);
            if (stravaAccessToken == null || stravaAccessToken.getAccessToken() == null) {
                throw new OAuthException(OAUTH_EXCEPTION_INVALID_TOKEN_MESSAGE + response + "'", null);
            }
            String accessTokenValue = stravaAccessToken.getAccessToken();
            return new Token(accessTokenValue, "", response);
        } catch (IOException e) {
            throw new OAuthException(OAUTH_EXCEPTION_INVALID_TOKEN_MESSAGE + response + "'", null);
        }
    }

    /**
     * inner class encapsulating the response of the access token request from Strava
     */
    private static class StravaAccessToken {
        /**
         * the access_token json property
         */
        @JsonProperty("access_token")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }


}
