package org.pac4j.kerberos.client.direct;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidation;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * Tests the Kerberos client with mocked Kerberos authenticator/validator (i.e. no real tickets used)
 *
 * @author Garry Boyce
 * @author Vidmantas Zemleris
 * @since 2.1.0
 */
public class KerberosClientTests implements TestsConstants {

    private KerberosAuthenticator kerberosAuthenticator;
    private KerberosTicketValidator krbValidator;

    private final static byte[] KERBEROS_TICKET = Base64.getEncoder().encode("Test Kerberos".getBytes(StandardCharsets.UTF_8));

    @Before
    public void before() {
        // mocking
        this.kerberosAuthenticator = mock(KerberosAuthenticator.class);
        this.krbValidator = mock(KerberosTicketValidator.class);
    }

    @Test
    public void testMissingKerberosAuthenticator() {
        final DirectKerberosClient kerberosClient = new DirectKerberosClient(null);
        TestsHelper.initShouldFail(kerberosClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectKerberosClient kerberosClient = new DirectKerberosClient(kerberosAuthenticator);
        kerberosClient.setProfileCreator(null);
        TestsHelper.initShouldFail(kerberosClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectKerberosClient kerberosClient = new DirectKerberosClient(kerberosAuthenticator);
        kerberosClient.init();
    }

    @Test
    public void testMissingKerberosHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        final DirectKerberosClient client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        final Optional<KerberosCredentials> credentials = client.getCredentials(new JEEContext(request, response));
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testAuthentication() {
        when(krbValidator.validateTicket(any())).thenReturn(new KerberosTicketValidation("garry", null, null, null));
        final DirectKerberosClient client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        final MockWebContext context = MockWebContext.create();

        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + new String(KERBEROS_TICKET, StandardCharsets.UTF_8));
        final KerberosCredentials credentials = client.getCredentials(context).get();
        assertEquals(new String(Base64.getDecoder().decode(KERBEROS_TICKET), StandardCharsets.UTF_8),
            new String(credentials.getKerberosTicket(), StandardCharsets.UTF_8));

        final CommonProfile profile = (CommonProfile) client.getUserProfile(credentials, context).get();
        assertEquals("garry", profile.getId());
    }
}
