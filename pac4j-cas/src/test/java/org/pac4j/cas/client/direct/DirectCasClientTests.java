package org.pac4j.cas.client.direct;

import lombok.val;
import org.apereo.cas.client.validation.AssertionImpl;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.pac4j.core.util.CommonHelper.addParameter;

/**
 * Tests the {@link DirectCasClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class DirectCasClientTests implements TestsConstants {

    @Test
    public void testInitOk() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val client = new DirectCasClient(configuration);
        client.init();
    }

    @Test
    public void testInitMissingConfiguration() {
        val client = new DirectCasClient();
        TestsHelper.expectException(client::init, TechnicalException.class, "configuration cannot be null");
    }

    @Test
    public void testInitGatewayForbidden() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setGateway(true);
        val client = new DirectCasClient(configuration);
        TestsHelper.expectException(client::init, TechnicalException.class,
            "the DirectCasClient can not support gateway to avoid infinite loops");
    }

    @Test
    public void testNoTokenRedirectionExpected() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val client = new DirectCasClient(configuration);
        val context = MockWebContext.create();
        context.setFullRequestURL(CALLBACK_URL);
        val action = (HttpAction) TestsHelper.expectException(()
            -> client.getCredentials(new CallContext(context, new MockSessionStore())));
        assertEquals(302, action.getCode());
        assertEquals(addParameter(LOGIN_URL, CasConfiguration.SERVICE_PARAMETER, CALLBACK_URL),
            ((FoundAction) action).getLocation());
    }

    @Test
    public void testTicketExistsValidationOccurs() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setDefaultTicketValidator((ticket, service) -> {
            if (TICKET.equals(ticket) && CALLBACK_URL.equals(service)) {
                return new AssertionImpl(TICKET);
            }
            throw new TechnicalException("Bad ticket or service");
        });
        val client = new DirectCasClient(configuration);
        val context = MockWebContext.create();
        context.setFullRequestURL(CALLBACK_URL + "?" + CasConfiguration.TICKET_PARAMETER + "=" + TICKET);
        context.addRequestParameter(CasConfiguration.TICKET_PARAMETER, TICKET);
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = client.getCredentials(ctx).get();
        assertEquals(TICKET, ((TokenCredentials) credentials).getToken());
        val newCredentials = client.validateCredentials(ctx, credentials).get();
        val profile = newCredentials.getUserProfile();
        assertTrue(profile instanceof CasProfile);
        assertEquals(TICKET, profile.getId());
    }
}
