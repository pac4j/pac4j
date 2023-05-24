package org.pac4j.kerberos.credentials.authenticator;

import org.ietf.jgss.GSSContext;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.util.HashSet;
import java.util.Set;

/**
 * Result of ticket validation
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class KerberosTicketValidation {

    private final String username;
    private final byte[] responseToken;
    private final GSSContext gssContext;
    private final String servicePrincipal;

    /**
     * <p>Constructor for KerberosTicketValidation.</p>
     *
     * @param username a {@link String} object
     * @param servicePrincipal a {@link String} object
     * @param responseToken an array of {@link byte} objects
     * @param gssContext a GSSContext object
     */
    public KerberosTicketValidation(String username, String servicePrincipal, byte[] responseToken, GSSContext gssContext) {
        this.username = username;
        this.servicePrincipal = servicePrincipal;
        if (responseToken != null) {
            this.responseToken = responseToken.clone();
        } else {
            this.responseToken = null;
        }
        this.gssContext = gssContext;
    }

    /**
     * <p>username.</p>
     *
     * @return a {@link String} object
     */
    public String username() {
        return username;
    }

    /**
     * <p>responseToken.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] responseToken() {
        return responseToken.clone();
    }

    /**
     * <p>Getter for the field <code>gssContext</code>.</p>
     *
     * @return a GSSContext object
     */
    public GSSContext getGssContext() {
        return gssContext;
    }

    /**
     * <p>subject.</p>
     *
     * @return a {@link Subject} object
     */
    public Subject subject() {
        final Set<KerberosPrincipal> princs = new HashSet<>();
        princs.add(new KerberosPrincipal(servicePrincipal));
        return new Subject(false, princs, new HashSet<>(), new HashSet<>());
    }
}
