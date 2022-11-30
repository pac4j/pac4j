package org.pac4j.kerberos.client.direct;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidation;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        val kerberosClient = new DirectKerberosClient(null);
        TestsHelper.initShouldFail(kerberosClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val kerberosClient = new DirectKerberosClient(kerberosAuthenticator);
        kerberosClient.setProfileCreator(null);
        TestsHelper.initShouldFail(kerberosClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val kerberosClient = new DirectKerberosClient(kerberosAuthenticator);
        kerberosClient.init();
    }

    @Test
    public void testMissingKerberosHeader() {
        val client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        val credentials = client.getCredentials(MockWebContext.create(), new MockSessionStore(),
            ProfileManagerFactory.DEFAULT);
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testWWWAuthenticateNegotiateHeaderIsSetToTriggerSPNEGOWhenNoCredentialsAreFound() {
        final WebContext context = MockWebContext.create();
        val client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        val credentials = client.getCredentials(context, new MockSessionStore(),
            ProfileManagerFactory.DEFAULT);
        assertFalse(credentials.isPresent());
        assertEquals("Negotiate", context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testAuthentication() {
        when(krbValidator.validateTicket(any())).thenReturn(new KerberosTicketValidation("garry", null, null, null));
        val client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        val context = MockWebContext.create();

        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + new String(KERBEROS_TICKET, StandardCharsets.UTF_8));
        val credentials = (KerberosCredentials) client.getCredentials(context, new MockSessionStore(),
            ProfileManagerFactory.DEFAULT).get();
        assertEquals(new String(Base64.getDecoder().decode(KERBEROS_TICKET), StandardCharsets.UTF_8),
            new String(credentials.getKerberosTicket(), StandardCharsets.UTF_8));

        val profile = (CommonProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals("garry", profile.getId());
    }
}
