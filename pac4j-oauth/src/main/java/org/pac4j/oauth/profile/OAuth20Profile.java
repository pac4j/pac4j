package org.pac4j.oauth.profile;

import org.pac4j.core.profile.CommonProfile;

/**
 * This class is the base OAuth 2.0 profile, extending from the base {@link org.pac4j.core.profile.CommonProfile}. It deals with the OAuth
 * access token.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class OAuth20Profile extends CommonProfile {

    private static final long serialVersionUID = -2313972372691233648L;

    private transient static final String ACCESS_TOKEN = "access_token";
    private transient static final String REFRESH_TOKEN = "refresh_token";

    public void setAccessToken(final String accessToken) {
        addAttribute(ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken() {
        return (String) getAttribute(ACCESS_TOKEN);
    }

    public void setRefreshToken(final String refreshToken) {
        addAttribute(REFRESH_TOKEN, refreshToken);
    }

    public String getRefreshToken() {
        return (String) getAttribute(REFRESH_TOKEN);
    }

    @Override
    public void removeLoginData() {
        removeAttribute(ACCESS_TOKEN);
        removeAttribute(REFRESH_TOKEN);
    }
}
