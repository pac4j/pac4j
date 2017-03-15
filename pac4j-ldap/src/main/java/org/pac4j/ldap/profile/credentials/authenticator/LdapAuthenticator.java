package org.pac4j.ldap.profile.credentials.authenticator;

import org.ldaptive.auth.Authenticator;
import org.pac4j.ldap.profile.service.LdapProfileService;

/**
 * Use the {@link LdapProfileService} instead.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 * @deprecated
 */
@Deprecated
public class LdapAuthenticator extends LdapProfileService {

    public LdapAuthenticator() {}

    public LdapAuthenticator(final Authenticator ldapAuthenticator) {
        super(ldapAuthenticator);
    }

    public LdapAuthenticator(final Authenticator ldapAuthenticator, final String attributes) {
        super(ldapAuthenticator, attributes);
    }
}
