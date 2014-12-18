package org.pac4j.oauth.profile.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Access token from Strava comes encapsulated in a Json string. This class simply maps this Json string to a java bean.
 *
 * @author Adrian Papusoi
 */
public class StravaAccessToken {
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
