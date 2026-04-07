package org.pac4j.cas.client.rest;

import lombok.val;
import org.apereo.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.jupiter.api.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.test.util.TestsConstants;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link CasRestBasicAuthClient} and {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CasRestClientTests implements TestsConstants {

    private final static String CAS_PREFIX_URL = "https://casserverpac4j.herokuapp.com/";
    private final static String USER = "jleleu";

    private CasConfiguration getConfig() {
        val config = new CasConfiguration();
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
        val client = new CasRestFormClient();
        client.setConfiguration(getConfig());
        client.setAuthenticator(authenticator);

        val context = MockWebContext.create();
        context.addRequestParameter(client.getUsernameParameter(), USER);
        context.addRequestParameter(client.getPasswordParameter(), USER);

        val ctx = new CallContext(context, new MockSessionStore());
        var credentials = (UsernamePasswordCredentials) client.getCredentials(ctx).get();
        credentials = (UsernamePasswordCredentials) client.validateCredentials(ctx, credentials).get();
        val profile = (CasRestProfile) client.getUserProfile(ctx, credentials).get();
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        val casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        val casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds, context);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertTrue(casProfile.getAttributes().size() > 0);
    }

    @Test
    public void testRestBasic() {
        internalTestRestBasic(new CasRestBasicAuthClient(getConfig(), VALUE, NAME), 13);
    }

    @Test
    public void testRestBasicWithCas20TicketValidator() {
        val config = getConfig();
        config.setDefaultTicketValidator(new Cas20ServiceTicketValidator(CAS_PREFIX_URL));
        internalTestRestBasic(new CasRestBasicAuthClient(config, VALUE, NAME), 13);
    }

    private void internalTestRestBasic(final CasRestBasicAuthClient client, int nbAttributes) {
        val context = MockWebContext.create();
        val token = USER + ":" + USER;
        context.addRequestHeader(VALUE, NAME + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)));

        val ctx = new CallContext(context, new MockSessionStore());
        var credentials = (UsernamePasswordCredentials) client.getCredentials(ctx).get();
        credentials = (UsernamePasswordCredentials) client.validateCredentials(ctx, credentials).get();
        val profile = (CasRestProfile) client.getUserProfile(ctx, credentials).get();
        assertEquals(USER, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        val casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        val casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds, context);
        assertNotNull(casProfile);
        assertEquals(USER, casProfile.getId());
        assertEquals(nbAttributes, casProfile.getAttributes().size());
        client.destroyTicketGrantingTicket(profile, context);

        try {
            client.requestServiceTicket(PAC4J_BASE_URL, profile, context);
        } catch (final TechnicalException e) {
            val msg = e.getMessage();
            assertTrue(msg.startsWith("Service ticket request for `CasRestProfile(super=CommonProfile(super=BasicUserProfile(logger=Logger["
                + "org.pac4j.cas.profile.CasRestProfile], id=" + USER + ", attributes={$tgt_key="));
            assertTrue(msg.endsWith(" could not be found or is considered invalid]"));
        }
    }
}
