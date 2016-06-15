package org.pac4j.kerberos.client.direct;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.DeferredHttpAction;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidation;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidator;



public class KerberosClientTests implements TestsConstants {
	
    private KerberosAuthenticator kerberosAuthenticator;
    private KerberosTicketValidator krbValidator;
    
    private final static byte[] KERBEROS_TICKET =  Base64.getEncoder().encode("Test Kerberos".getBytes());
    
    @Before
    public void before() {
        // mocking
        this.kerberosAuthenticator = mock(KerberosAuthenticator.class);
        this.krbValidator = mock(KerberosTicketValidator.class);
    }
    
    @Test
    public void testMissingKerberosAuthenticator() {
        final KerberosClient kerberosClient = new KerberosClient(null);
        TestsHelper.initShouldFail(kerberosClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final KerberosClient kerberosClient = new KerberosClient(kerberosAuthenticator);
        kerberosClient.setProfileCreator(null);
        TestsHelper.initShouldFail(kerberosClient, "profileCreator cannot be null");
    }

    @Test
    public void testBadAuthenticatorType() {
        final KerberosClient kerberosClient = new KerberosClient(new DummyAuthenticator());
        TestsHelper.initShouldFail(kerberosClient, "Unsupported authenticator type: class org.pac4j.kerberos.client.direct.DummyAuthenticator");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final KerberosClient kerberosClient = new KerberosClient(kerberosAuthenticator);
        kerberosClient.init(null);
    }
    
    @Test(expected=DeferredHttpAction.class)
    public void testMissingKerberosHeader() throws HttpAction {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
        final KerberosClient client = new KerberosClient(new KerberosAuthenticator(krbValidator));
        client.getCredentials(new J2EContext(request, response));
    }
    
    @Test
    public void testAuthentication() throws HttpAction, UnsupportedEncodingException {
    	when(krbValidator.validateTicket(any())).thenReturn(new KerberosTicketValidation("garry", null,null,null));
        final KerberosClient client = new KerberosClient(new KerberosAuthenticator(krbValidator));
        final MockWebContext context = MockWebContext.create();

        byte[] kerberosTicket =  Base64.getEncoder().encode("Test Kerberos".getBytes());
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Negotiate " + new String(kerberosTicket, "UTF-8"));
        final KerberosCredentials credentials = client.getCredentials(context);
        assertEquals(new String(Base64.getDecoder().decode(KERBEROS_TICKET), "UTF-8"), new String(credentials.getKerberosTicket(), "UTF-8"));

        final CommonProfile profile = client.getUserProfile(credentials, context);
        assertEquals("garry", profile.getId());
    } 

}
