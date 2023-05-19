package org.pac4j.oauth.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * This class is the base OAuth 2.0 profile, extending from the base {@link CommonProfile}. It deals with the OAuth
 * access token.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
@ToString(callSuper = true)
public class OAuth20Profile extends CommonProfile {

    @Serial
    private static final long serialVersionUID = -2313972372691233648L;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    /**
     * <p>setAccessToken.</p>
     *
     * @param accessToken a {@link String} object
     */
    public void setAccessToken(final String accessToken) {
        addAttribute(ACCESS_TOKEN, accessToken);
    }

    /**
     * <p>getAccessToken.</p>
     *
     * @return a {@link String} object
     */
    public String getAccessToken() {
        return (String) getAttribute(ACCESS_TOKEN);
    }

    /**
     * <p>setRefreshToken.</p>
     *
     * @param refreshToken a {@link String} object
     */
    public void setRefreshToken(final String refreshToken) {
        addAttribute(REFRESH_TOKEN, refreshToken);
    }

    /**
     * <p>getRefreshToken.</p>
     *
     * @return a {@link String} object
     */
    public String getRefreshToken() {
        return (String) getAttribute(REFRESH_TOKEN);
    }

    /** {@inheritDoc} */
    @Override
    public void removeLoginData() {
        removeAttribute(ACCESS_TOKEN);
        removeAttribute(REFRESH_TOKEN);
    }
}
