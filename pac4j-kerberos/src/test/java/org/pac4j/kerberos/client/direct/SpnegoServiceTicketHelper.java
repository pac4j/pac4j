package org.pac4j.kerberos.client.direct;

import org.apache.kerby.kerberos.kerb.client.JaasKrbUtil;
import org.ietf.jgss.*;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.Base64;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * based on code from  https://github.com/coheigea/testcases/apache/cxf/cxf-kerberos-kerby
 * see  https://goo.gl/awwEjs
 *
 * @author Colm O hEigeartaigh
 * @author Vidmantas Zemleris
 * @since 2.1.0
 */
class SpnegoServiceTicketHelper {
    /**
     * This class represents a PrivilegedExceptionAction implementation to obtain a service ticket from a Kerberos
     * Key Distribution Center.
     */
    private static class KerberosClientExceptionAction implements PrivilegedExceptionAction<byte[]> {

        private static final String JGSS_KERBEROS_TICKET_OID = "1.2.840.113554.1.2.2";

        private Principal clientPrincipal;
        private String serviceName;

        public KerberosClientExceptionAction(final Principal clientPrincipal, final String serviceName) {
            this.clientPrincipal = clientPrincipal;
            this.serviceName = serviceName;
        }

        @Override
        public byte[] run() throws GSSException {
            final GSSManager gssManager = GSSManager.getInstance();

            final GSSName gssService = gssManager.createName(serviceName, GSSName.NT_HOSTBASED_SERVICE);
            final Oid oid = new Oid(JGSS_KERBEROS_TICKET_OID);
            final GSSName gssClient = gssManager.createName(clientPrincipal.getName(), GSSName.NT_USER_NAME);
            final GSSCredential credentials =
                gssManager.createCredential(
                    gssClient, GSSCredential.DEFAULT_LIFETIME, oid, GSSCredential.INITIATE_ONLY
                );

            final GSSContext secContext =
                gssManager.createContext(
                    gssService, oid, credentials, GSSContext.DEFAULT_LIFETIME
                );

            secContext.requestMutualAuth(false);
            secContext.requestCredDeleg(false);

            final byte[] token = new byte[0];
            final byte[] returnedToken = secContext.initSecContext(token, 0, token.length);

            secContext.dispose();

            return returnedToken;
        }
    }

    public static String getGSSTicket(final String clientPrincipal, final String clientPassword,
                                      final String serviceName) throws Exception {
        final Subject clientSubject = JaasKrbUtil.loginUsingPassword(clientPrincipal, clientPassword);

        final Set<Principal> clientPrincipals = clientSubject.getPrincipals();
        assertFalse(clientPrincipals.isEmpty());

        // Get the TGT
        final Set<KerberosTicket> privateCredentials = clientSubject.getPrivateCredentials(KerberosTicket.class);
        assertFalse(privateCredentials.isEmpty());
        final KerberosTicket tgt = privateCredentials.iterator().next();
        assertNotNull(tgt);

        // Get the service ticket
        final KerberosClientExceptionAction action =
            new KerberosClientExceptionAction(clientPrincipals.iterator().next(), serviceName);
        final byte[] ticketBytes = Subject.doAs(clientSubject, action);
        assertNotNull(ticketBytes);

        return new String(Base64.getEncoder().encode(ticketBytes), StandardCharsets.UTF_8);
    }
}
