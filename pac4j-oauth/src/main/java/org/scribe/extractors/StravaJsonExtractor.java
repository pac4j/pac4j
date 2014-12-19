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
