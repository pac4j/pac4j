package org.pac4j.cas.client.rest;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.util.TestsHelper;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests the {@link CasRestBasicAuthClient} and {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CasRestClientIT implements TestsConstants {

    private final static String CAS_PREFIX_URL = "http://casserverpac4j.herokuapp.com/";
    private final static String USER = "jleleu";

    @Test
    public void testRestForm() throws HttpAction {
        internalTestRestForm(new CasRestAuthenticator(CAS_PREFIX_URL));
    }

    @Test
    public void testRestFormWithCaching() throws HttpAction {
        internalTestRestForm(new LocalCachingAuthenticator<>(new CasRestAuthenticator(CAS_PREFIX_URL), 100, 100, TimeUnit.SECONDS));
    }

    private void internalTestRestForm(final Authenticator authenticator) throws HttpAction {
        final CasRestFormClient client = new CasRestFormClient();
        client.setAuthenticator(authenticator);

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getUsernameParameter(), USER);
        context.addRequestParameter(client.getPasswordParameter(), USER);

        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final CasRestProfile profile = client.getUserProfile(credentials, context);
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final TokenCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertTrue(casProfile.getAttributes().size() > 0);
    }

    @Test
    public void testRestBasic() throws HttpAction, UnsupportedEncodingException {
        internalTestRestBasic(new CasRestBasicAuthClient(CAS_PREFIX_URL, VALUE, NAME), 3);
    }

    @Test
    public void testRestBasicWithCas20TicketValidator() throws HttpAction, UnsupportedEncodingException {
        final CasRestAuthenticator authenticator = new CasRestAuthenticator(CAS_PREFIX_URL);
        authenticator.setTicketValidator(new Cas20ServiceTicketValidator(CAS_PREFIX_URL));
        internalTestRestBasic(new CasRestBasicAuthClient(authenticator, VALUE, NAME), 0);
    }

    private void internalTestRestBasic(final CasRestBasicAuthClient client, int nbAttributes) throws HttpAction, UnsupportedEncodingException {
        final MockWebContext context = MockWebContext.create();
        final String token = USER + ":" + USER;
        context.addRequestHeader(VALUE, NAME + Base64.getEncoder().encodeToString(token.getBytes(HttpConstants.UTF8_ENCODING)));

        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final CasRestProfile profile = client.getUserProfile(credentials, context);
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final TokenCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertEquals(nbAttributes, casProfile.getAttributes().size());
        client.destroyTicketGrantingTicket(profile);

        TestsHelper.expectException(() -> client.requestServiceTicket(PAC4J_BASE_URL, profile), TechnicalException.class,
                "Service ticket request for `#CasRestProfile# | id: " + USER + " | attributes: {} | roles: [] | permissions: [] | isRemembered: false |` failed: (404) Not Found");
    }
}
