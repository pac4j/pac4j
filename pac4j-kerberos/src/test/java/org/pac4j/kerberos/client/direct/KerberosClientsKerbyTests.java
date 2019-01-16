package org.pac4j.kerberos.client.direct;

import org.apache.kerby.kerberos.kdc.impl.NettyKdcServerImpl;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.kerberos.client.indirect.IndirectKerberosClient;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.SunJaasKerberosTicketValidator;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * This tests both Direct and Indirect Kerberos clients.
 * Both clients behave the same except then when credentials are invalid or not specified.
 * - .getCredentials() in direct client
 *   * returns NULL
 * - .getCredentials() in indirect client raises an exception:
 *   * raises a HttpAction "401 Authenticate: Negotiate"
 *
 * This is a test with real kerberos ticket validation:
 * - spins up a mini/lightweight Kerberos server (using Apache Kerby)
 * - generates the keytabs for service and client
 * - generates a real service ticket, and passes it to mock context
 * - checks that the ticket is correctly validated, and yields a correct client ID
 *
 * @author Vidmantas Zemleris, at Kensu.io
 * @since 2.1.0
 */
public class KerberosClientsKerbyTests implements TestsConstants {
    private static SimpleKdcServer kerbyServer;

    static String clientPrincipal = "clientPrincipal@MYREALM.LT";
    static String clientPassword = "clientPrincipal";

    static String servicePrincipal = "HTTP/lala.mydomain.de@MYREALM.LT"; // i.e. HTTP/full-qualified-domain-name@DOMAIN
    static String serviceName = "HTTP@lala.mydomain.de";

    static String serviceKeyTabFileName = "/tmp/testServiceKeyTabFile";
    static File serviceKeytabFile = new File(serviceKeyTabFileName);

    @BeforeClass
    public static void beforeAll() throws KrbException, IOException {
        setupKerbyServer();
    }

    @AfterClass
    public static void afterAll() throws KrbException {
        kerbyServer.stop();
    }

    private static void setupKerbyServer() throws KrbException, IOException {
        kerbyServer = new SimpleKdcServer();
        kerbyServer.setKdcHost("localhost");
        kerbyServer.setKdcRealm("MYREALM.LT");
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
    public void testDirectNoAuth() {
        // a request without "Authentication: (Negotiate|Kerberos) SomeToken" header, yields NULL credentials
        assertFalse(setupDirectKerberosClient().getCredentials(MockWebContext.create()).isPresent());
    }

    @Test
    public void testDirectAuthenticationWithRealTicket() throws Exception {
        checkWithGoodTicket(setupDirectKerberosClient());
    }


    // =====================
    // Indirect client below
    // =====================

    @Test
    public void testDirectIncorrectAuth() {
        // a request with an incorrect Kerberos token, yields NULL credentials also
        final MockWebContext context = MockWebContext.create()
            .addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + "AAAbbAA123");
        assertFalse(setupDirectKerberosClient().getCredentials(context).isPresent());
    }

    @Test
    public void testIndirectNoAuth() {
        // a request without "Authentication: (Negotiate|Kerberos) SomeToken" header
        assertGetCredentialsFailsWithAuthRequired(setupIndirectKerberosClient(), MockWebContext.create(),"Performing a 401 HTTP action");
    }

    @Test
    public void testIndirectIncorrectAuth() {
        // a request with an incorrect Kerberos token, yields NULL credentials also
        final MockWebContext context = MockWebContext.create()
            .addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + "AAAbbAA123");
        assertGetCredentialsFailsWithAuthRequired(setupIndirectKerberosClient(), context, "Performing a 401 HTTP action");
    }

    @Test
    public void testIndirectAuthenticationWithRealTicket() throws Exception {
        checkWithGoodTicket(setupIndirectKerberosClient());
    }

    // ===============================
    // Test helpers
    // ===============================


    private void assertGetCredentialsFailsWithAuthRequired(
        IndirectKerberosClient kerbClient,
        MockWebContext context,
        String expectedMsg) {
        try {
            kerbClient.getCredentials(context);
            fail("should throw HttpAction");
        } catch (final HttpAction e) {
            assertEquals(401, e.getCode());
            assertEquals("Negotiate", context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
            assertEquals(expectedMsg, e.getMessage());
        }
    }

    private void checkWithGoodTicket(Client<KerberosCredentials> client) throws Exception {
        String spnegoWebTicket = SpnegoServiceTicketHelper.getGSSTicket(clientPrincipal, clientPassword, serviceName);

        // mock web request
        final MockWebContext context = mockWebRequestContext(spnegoWebTicket);
        final Optional<KerberosCredentials> credentials = client.getCredentials(context);
        assertTrue(credentials.isPresent());
        System.out.println(credentials.get());

        final KerberosProfile profile = (KerberosProfile) client.getUserProfile(credentials.get(), context);
        assertNotNull(profile);
        assertEquals(clientPrincipal, profile.getId());
    }

    private DirectKerberosClient setupDirectKerberosClient() {
        return new DirectKerberosClient(new KerberosAuthenticator(getKerberosValidator()));
    }

    private IndirectKerberosClient setupIndirectKerberosClient() {
        IndirectKerberosClient client = new IndirectKerberosClient(new KerberosAuthenticator(getKerberosValidator()));
        client.setCallbackUrl("http://dummy.com/");
        return client;
    }

    private SunJaasKerberosTicketValidator getKerberosValidator() {
        SunJaasKerberosTicketValidator validator = new SunJaasKerberosTicketValidator();
        validator.setServicePrincipal(servicePrincipal);
        validator.setKeyTabLocation(new FileSystemResource(serviceKeytabFile));
        validator.setDebug(true);
        return validator;
    }

    private MockWebContext mockWebRequestContext(String spnegoWebTicket) {
        System.out.println("spnegoWebTicket:" + spnegoWebTicket);
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + spnegoWebTicket);
        return context;
    }
}
