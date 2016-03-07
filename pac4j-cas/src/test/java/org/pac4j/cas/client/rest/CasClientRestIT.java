package org.pac4j.cas.client.rest;

import org.apache.commons.codec.binary.Base64;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.HttpTGTProfile;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.Authenticator;
import org.pac4j.http.credentials.authenticator.LocalCachingAuthenticator;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests the CAS REST clients.
 *
 * @author Jerome Leleu
 * @since 1.8.6
 */
public final class CasClientRestIT implements TestsConstants {

    private final static String CAS_REST_URL = "http://casserverpac4j.herokuapp.com/";

    @Test
    public void testRestForm() throws RequiresHttpAction {
        internalTestRestForm(new CasRestAuthenticator(CAS_REST_URL));
    }

    @Test
    public void testRestFormWithCaching() throws RequiresHttpAction {
        internalTestRestForm(new LocalCachingAuthenticator(new CasRestAuthenticator(CAS_REST_URL), 100, 100, TimeUnit.SECONDS));
    }

    private void internalTestRestForm(final Authenticator authenticator) throws RequiresHttpAction {
        final CasRestFormClient client = new CasRestFormClient(authenticator);

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getUsername(), USERNAME);
        context.addRequestParameter(client.getPassword(), USERNAME);

        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final HttpTGTProfile profile = (HttpTGTProfile) client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final CasCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        assertEquals(USERNAME, casProfile.getId());
        assertEquals(0, casProfile.getAttributes().size());
    }

    @Test
    public void testRestBasic() throws RequiresHttpAction {
        final CasRestAuthenticator authenticator = new CasRestAuthenticator(CAS_REST_URL);
        authenticator.setTicketValidator(new Cas30ServiceTicketValidator(CAS_REST_URL));
        final CasRestBasicAuthClient client = new CasRestBasicAuthClient(authenticator, VALUE, NAME);

        final MockWebContext context = MockWebContext.create();
        final String token = USERNAME + ":" + USERNAME;
        context.addRequestHeader(VALUE, NAME + Base64.encodeBase64String(token.getBytes()));

        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final HttpTGTProfile profile = (HttpTGTProfile) client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final CasCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        assertEquals(USERNAME, casProfile.getId());
        assertEquals(3, casProfile.getAttributes().size());
        client.destroyTicketGrantingTicket(context, profile);

        try {
            client.requestServiceTicket(PAC4J_BASE_URL, profile);
            fail("shoud fail");
        } catch (final TechnicalException e) {
            assertEquals("Service ticket request for `<HttpTGTProfile> | id: username | attributes: {} | roles: [] | permissions: [] | isRemembered: false |` failed: (404) Not Found", e.getMessage());
        }
    }
}
