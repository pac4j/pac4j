package org.pac4j.http.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

/**
 * Profile for IP authentication.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@ToString(callSuper = true)
public class IpProfile extends CommonProfile {

    private static final long serialVersionUID = -4017369503998168023L;
}
