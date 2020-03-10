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

    public KerberosTicketValidation(final String username, final String servicePrincipal,
                                    final byte[] responseToken, final GSSContext gssContext) {
        this.username = username;
        this.servicePrincipal = servicePrincipal;
        if (responseToken != null) {
            this.responseToken = responseToken.clone();
        } else {
            this.responseToken = null;
        }
        this.gssContext = gssContext;
    }

    public String username() {
        return username;
    }

    public byte[] responseToken() {
        return responseToken.clone();
    }

    public GSSContext getGssContext() {
        return gssContext;
    }

    public Subject subject() {
        final Set<KerberosPrincipal> princs = new HashSet<>();
        princs.add(new KerberosPrincipal(servicePrincipal));
        return new Subject(false, princs, new HashSet<>(), new HashSet<>());
    }
}
