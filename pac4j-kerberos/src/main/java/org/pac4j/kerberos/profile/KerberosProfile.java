package org.pac4j.kerberos.profile;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.ietf.jgss.GSSContext;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * Represents a user profile based on a Kerberos authentication.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class KerberosProfile extends CommonProfile {

    @Serial
    private static final long serialVersionUID = -1388563485891552197L;
    private GSSContext gssContext = null;

    /**
     * <p>Constructor for KerberosProfile.</p>
     */
    public KerberosProfile() {
    }

    /**
     * <p>Constructor for KerberosProfile.</p>
     *
     * @param gssContext a GSSContext object
     */
    public KerberosProfile(final GSSContext gssContext) {
        this.gssContext = gssContext;
    }

    /**
     * <p>Getter for the field <code>gssContext</code>.</p>
     *
     * @return a GSSContext object
     */
    public GSSContext getGssContext() {
        return gssContext;
    }
}
