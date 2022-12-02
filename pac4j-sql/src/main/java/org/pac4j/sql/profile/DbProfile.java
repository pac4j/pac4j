package org.pac4j.sql.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

/**
 * <p>The user profile returned by a DB authentication.</p>
 *
 * @see org.pac4j.sql.profile.service.DbProfileService
 * @author Jerome Leleu
 * @since 1.8.0
 */
@ToString(callSuper = true)
public class DbProfile extends CommonProfile {

    private static final long serialVersionUID = 4740352872728540613L;

}
