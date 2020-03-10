package org.pac4j.cas.client.rest;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
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
        final CasConfiguration config = new CasConfiguration();
        config.setPrefixUrl(CAS_PREFIX_URL);
        return config;
    }

    @Test
    public void testRestForm() {
        internalTestRestForm(new CasRestAuthenticator(getConfig()));
    }

    @Test
    public void testRestFormWithCaching() {
        internalTestRestForm(new LocalCachingAuthenticator<>(new CasRestAuthenticator(getConfig()), 100, 100, TimeUnit.SECONDS));
    }

    private void internalTestRestForm(final Authenticator authenticator) {
        final CasRestFormClient client = new CasRestFormClient();
        client.setConfiguration(getConfig());
        client.setAuthenticator(authenticator);

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getUsernameParameter(), USER);
        context.addRequestParameter(client.getPasswordParameter(), USER);

        final UsernamePasswordCredentials credentials = client.getCredentials(context).get();
        final CasRestProfile profile = (CasRestProfile) client.getUserProfile(credentials, context).get();
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final TokenCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds, context);
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
        final CasConfiguration config = getConfig();
        config.setDefaultTicketValidator(new Cas20ServiceTicketValidator(CAS_PREFIX_URL));
        internalTestRestBasic(new CasRestBasicAuthClient(config, VALUE, NAME), 0);
    }

    private void internalTestRestBasic(final CasRestBasicAuthClient client, final int nbAttributes) {
        final MockWebContext context = MockWebContext.create();
        final String token = USER + ":" + USER;
        context.addRequestHeader(VALUE, NAME + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)));

        final UsernamePasswordCredentials credentials = client.getCredentials(context).get();
        final CasRestProfile profile = (CasRestProfile) client.getUserProfile(credentials, context).get();
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final TokenCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds, context);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertEquals(nbAttributes, casProfile.getAttributes().size());
        client.destroyTicketGrantingTicket(profile, context);

        TestsHelper.expectException(() -> client.requestServiceTicket(PAC4J_BASE_URL, profile, context), TechnicalException.class,
                "Service ticket request for `#CasRestProfile# | id: " + USER + " | attributes: {} | roles: [] | permissions: [] | "
                    + "isRemembered: false | clientName: CasRestBasicAuthClient | linkedId: null |` failed: (404) Not Found");
    }
}
