package org.pac4j.oauth.profile.linkedin2;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * LinkedIn OAuth configuration.
 *
 * @author Jerome Leleu
 * @author Vassilis Virvilis
 * @since 3.0.0
 */
public class LinkedIn2Configuration extends OAuth20Configuration {
    public static final String DEFAULT_SCOPE = "r_liteprofile r_emailaddress";

    private String profileUrl = "https://api.linkedin.com/v2/me?projection=(id,"
        + LinkedIn2ProfileDefinition.LOCALIZED_FIRST_NAME
        + ',' + LinkedIn2ProfileDefinition.LOCALIZED_LAST_NAME
        + ',' + LinkedIn2ProfileDefinition.PROFILE_PICTURE + "(displayImage~:playableStreams))";

    public LinkedIn2Configuration() {
        setScope(DEFAULT_SCOPE);
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
