package org.pac4j.ldap.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

/**
 * The user profile returned by a LDAP authentication.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@ToString(callSuper = true)
public class LdapProfile extends CommonProfile {

    private static final long serialVersionUID = 4745130273071234466L;
}
