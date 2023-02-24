package org.pac4j.oauth.profile.linkedin2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * LinkedIn OAuth configuration.
 *
 * @author Jerome Leleu
 * @author Vassilis Virvilis
 * @since 3.0.0
 */
@ToString(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@With
@AllArgsConstructor
public class LinkedIn2Configuration extends OAuth20Configuration {
    /** Constant <code>DEFAULT_SCOPE="r_liteprofile r_emailaddress"</code> */
    public static final String DEFAULT_SCOPE = "r_liteprofile r_emailaddress";

    private String profileUrl = "https://api.linkedin.com/v2/me?projection=(id,"
        + LinkedIn2ProfileDefinition.LOCALIZED_FIRST_NAME
        + ',' + LinkedIn2ProfileDefinition.LOCALIZED_LAST_NAME
        + ',' + LinkedIn2ProfileDefinition.PROFILE_PICTURE + "(displayImage~:playableStreams))";

    /**
     * <p>Constructor for LinkedIn2Configuration.</p>
     */
    public LinkedIn2Configuration() {
        setScope(DEFAULT_SCOPE);
    }
}
