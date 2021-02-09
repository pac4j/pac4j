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

        public KerberosClientExceptionAction(Principal clientPrincipal, String serviceName) {
            this.clientPrincipal = clientPrincipal;
            this.serviceName = serviceName;
        }

        @Override
        public byte[] run() throws GSSException {
            var gssManager = GSSManager.getInstance();

            var gssService = gssManager.createName(serviceName, GSSName.NT_HOSTBASED_SERVICE);
            var oid = new Oid(JGSS_KERBEROS_TICKET_OID);
            var gssClient = gssManager.createName(clientPrincipal.getName(), GSSName.NT_USER_NAME);
            var credentials =
                gssManager.createCredential(
                    gssClient, GSSCredential.DEFAULT_LIFETIME, oid, GSSCredential.INITIATE_ONLY
                );

            var secContext =
                gssManager.createContext(
                    gssService, oid, credentials, GSSContext.DEFAULT_LIFETIME
                );

            secContext.requestMutualAuth(false);
            secContext.requestCredDeleg(false);

            var token = new byte[0];
            var returnedToken = secContext.initSecContext(token, 0, token.length);

            secContext.dispose();

            return returnedToken;
        }
    }

    public static String getGSSTicket(String clientPrincipal, String clientPassword, String serviceName) throws Exception {
        var clientSubject = JaasKrbUtil.loginUsingPassword(clientPrincipal, clientPassword);

        var clientPrincipals = clientSubject.getPrincipals();
        assertFalse(clientPrincipals.isEmpty());

        // Get the TGT
        var privateCredentials = clientSubject.getPrivateCredentials(KerberosTicket.class);
        assertFalse(privateCredentials.isEmpty());
        var tgt = privateCredentials.iterator().next();
        assertNotNull(tgt);

        // Get the service ticket
        var action =
            new KerberosClientExceptionAction(clientPrincipals.iterator().next(), serviceName);
        var ticketBytes = Subject.doAs(clientSubject, action);
        assertNotNull(ticketBytes);

        return new String(Base64.getEncoder().encode(ticketBytes), StandardCharsets.UTF_8);
    }
}
