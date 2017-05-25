package org.pac4j.kerberos.client.direct;

import org.apache.kerby.kerberos.kdc.impl.NettyKdcServerImpl;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.SunJaasKerberosTicketValidator;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This is a test with real kerberos ticket validation:
 * - spins up a mini/lightweight Kerberos server (using Apache Kerby)
 * - generates the keytabs for service and client
 * - generates a real service ticket, and passes it to mock context
 * - checks that the ticket is correctly validated, and yields a correct client ID
 *
 * @author Vidmantas Zemleris, at Kensu.io
 */
public class KerberosClientKerbyTests implements TestsConstants {
    private static SimpleKdcServer kerbyServer;

    String clientPrincipal = "clientPrincipal@MYREALM.LT";
    String clientPassword = "clientPrincipal";

    String servicePrincipal = "HTTP/lala.mydomain.de@MYREALM.LT"; // i.e. HTTP/full-qualified-domain-name@DOMAIN
    String serviceName = "HTTP@lala.mydomain.de";

    String serviceKeyTabFileName = "/tmp/testServiceKeyTabFile";
    File serviceKeytabFile = new File(serviceKeyTabFileName);

    @Before
    public void before() throws KrbException, IOException {
        setupKerbyServer();
    }

    private void setupKerbyServer() throws KrbException, IOException {
        kerbyServer = new SimpleKdcServer();
        kerbyServer.setKdcHost("localhost");
        kerbyServer.setKdcRealm("MYREALM.LT"); // FIXME
        kerbyServer.setAllowUdp(true);
        //kerbyServer.setWorkDir(new File(basedir + "/target"));
        kerbyServer.setInnerKdcImpl(new NettyKdcServerImpl(kerbyServer.getKdcSetting()));
        kerbyServer.init();

        // Create principals
        kerbyServer.createPrincipal(clientPrincipal, clientPassword);
        kerbyServer.createPrincipal(servicePrincipal, "servicePrincipal");
        kerbyServer.getKadmin().exportKeytab(serviceKeytabFile, servicePrincipal);
        //System.out.println(new String(Files.readAllBytes(serviceKeytabFile.toPath())));
        kerbyServer.start();
    }

    @Test
    public void testAuthenticationWithRealTicket() throws Exception {
        final KerberosClient client = setupKerberosClient();
        String spnegoWebTicket = SpnegoServiceTicketHelper.getGSSTicket(clientPrincipal, clientPassword, serviceName);

        // mock web request
        final MockWebContext context = mockWebRequestContext(spnegoWebTicket);
        final KerberosCredentials credentials = client.getCredentials(context);
        assertNotNull(credentials);
        System.out.println(credentials);

        final KerberosProfile profile = client.getUserProfile(credentials, context);
        assertNotNull(profile);
        assertEquals(clientPrincipal, profile.getId());
    }

    private KerberosClient setupKerberosClient() {
        SunJaasKerberosTicketValidator validator = new SunJaasKerberosTicketValidator();
        validator.setServicePrincipal(servicePrincipal);
        validator.setKeyTabLocation(new FileSystemResource(serviceKeytabFile));
        validator.setDebug(true);
        validator.reinit();
        return new KerberosClient(new KerberosAuthenticator(validator));
    }

    private MockWebContext mockWebRequestContext(String spnegoWebTicket) {
        System.out.println("spnegoWebTicket:" + spnegoWebTicket);
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + spnegoWebTicket);
        return context;
    }
}
