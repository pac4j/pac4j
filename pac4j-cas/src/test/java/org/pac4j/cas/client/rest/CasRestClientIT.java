package org.pac4j.cas.client.rest;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.util.TestsHelper;

import java.nio.charset.StandardCharsets;
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

    private CasConfiguration getConfig() {
        final var config = new CasConfiguration();
        config.setPrefixUrl(CAS_PREFIX_URL);
        return config;
    }

    @Test
    public void testRestForm() {
        internalTestRestForm(new CasRestAuthenticator(getConfig()));
    }

    @Test
    public void testRestFormWithCaching() {
        internalTestRestForm(new LocalCachingAuthenticator(new CasRestAuthenticator(getConfig()), 100, 100, TimeUnit.SECONDS));
    }

    private void internalTestRestForm(final Authenticator authenticator) {
        final var client = new CasRestFormClient();
        client.setConfiguration(getConfig());
        client.setAuthenticator(authenticator);

        final var context = MockWebContext.create();
        context.addRequestParameter(client.getUsernameParameter(), USER);
        context.addRequestParameter(client.getPasswordParameter(), USER);

        final var credentials =
            (UsernamePasswordCredentials) client.getCredentials(context, new MockSessionStore()).get();
        final var profile = (CasRestProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final var casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        final var casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds, context);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertTrue(casProfile.getAttributes().size() > 0);
    }

    @Test
    public void testRestBasic() {
        internalTestRestBasic(new CasRestBasicAuthClient(getConfig(), VALUE, NAME), 3);
    }

    @Test
    public void testRestBasicWithCas20TicketValidator() {
        final var config = getConfig();
        config.setDefaultTicketValidator(new Cas20ServiceTicketValidator(CAS_PREFIX_URL));
        internalTestRestBasic(new CasRestBasicAuthClient(config, VALUE, NAME), 0);
    }

    private void internalTestRestBasic(final CasRestBasicAuthClient client, int nbAttributes) {
        final var context = MockWebContext.create();
        final var token = USER + ":" + USER;
        context.addRequestHeader(VALUE, NAME + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)));

        final var credentials =
            (UsernamePasswordCredentials) client.getCredentials(context, new MockSessionStore()).get();
        final var profile = (CasRestProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final var casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        final var casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds, context);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertEquals(nbAttributes, casProfile.getAttributes().size());
        client.destroyTicketGrantingTicket(profile, context);

        TestsHelper.expectException(() -> client.requestServiceTicket(PAC4J_BASE_URL, profile, context), TechnicalException.class,
                "Service ticket request for `#CasRestProfile# | id: " + USER + " | attributes: {} | roles: [] | permissions: [] | "
                    + "isRemembered: false | clientName: CasRestBasicAuthClient | linkedId: null |` failed: (404) Not Found");
    }
}
