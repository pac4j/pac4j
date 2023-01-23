package org.pac4j.cas.client.direct;

import lombok.val;
import org.apereo.cas.client.validation.AssertionImpl;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * Tests the {@link DirectCasProxyClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class DirectCasProxyClientTests implements TestsConstants {

    @Test
    public void testInitOk() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setProtocol(CasProtocol.CAS20_PROXY);
        val client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        client.init();
    }

    @Test
    public void testInitMissingConfiguration() {
        val client = new DirectCasProxyClient();
        client.setServiceUrl(CALLBACK_URL);
        TestsHelper.expectException(client::init, TechnicalException.class, "configuration cannot be null");
    }

    @Test
    public void testInitMissingServiceUrl() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val client = new DirectCasProxyClient();
        client.setConfiguration(configuration);
        TestsHelper.expectException(client::init, TechnicalException.class, "serviceUrl cannot be blank");
    }

    @Test
    public void testInitFailsBadProtocol() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        TestsHelper.expectException(client::init, TechnicalException.class,
            "The DirectCasProxyClient must be configured with a CAS proxy protocol (CAS20_PROXY or CAS30_PROXY)");
    }

    @Test
    public void testNoTicket() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setProtocol(CasProtocol.CAS20_PROXY);
        val client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        assertFalse(client.getCredentials(new CallContext(MockWebContext.create(), new MockSessionStore())).isPresent());
    }

    @Test
    public void testTokenExistsValidationOccurs() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setProtocol(CasProtocol.CAS30_PROXY);
        configuration.setDefaultTicketValidator((ticket, service) -> {
            if (TICKET.equals(ticket) && CALLBACK_URL.equals(service)) {
                return new AssertionImpl(TICKET);
            }
            throw new TechnicalException("Bad ticket or service");
        });
        val client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        val context = MockWebContext.create();
        context.setFullRequestURL(CALLBACK_URL + "?" + CasConfiguration.TICKET_PARAMETER + "=" + TICKET);
        context.addRequestParameter(CasConfiguration.TICKET_PARAMETER, TICKET);
        val credentials = (TokenCredentials) client.getCredentials(new CallContext(context, new MockSessionStore())).get();
        assertEquals(TICKET, credentials.getToken());
        val profile = credentials.getUserProfile();
        assertTrue(profile instanceof CasProfile);
        assertEquals(TICKET, profile.getId());
    }
}
