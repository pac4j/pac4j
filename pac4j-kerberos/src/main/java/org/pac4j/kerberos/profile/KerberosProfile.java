package org.pac4j.kerberos.profile;

import lombok.ToString;
import org.ietf.jgss.GSSContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * Represents a user profile based on a Kerberos authentication.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
@ToString(callSuper = true)
public class KerberosProfile extends CommonProfile {

    private static final long serialVersionUID = -1388563485891552197L;
    private GSSContext gssContext = null;

    public KerberosProfile() {
    }

    public KerberosProfile(final GSSContext gssContext) {
        this.gssContext = gssContext;
    }

    public GSSContext getGssContext() {
        return gssContext;
    }
}
