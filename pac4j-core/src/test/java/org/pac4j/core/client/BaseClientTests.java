package org.pac4j.core.client;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * This class tests the {@link BaseClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class BaseClientTests implements TestsConstants {

    @Test
    public void testDirectClient() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        final Credentials credentials = client.getCredentials(context);
        assertNull(credentials);
    }

    @Test
    public void testIndirectClientWithImmediate() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE, false);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    @Test
    public void testNullCredentials() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE, false);
        final MockWebContext context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertNull(client.getUserProfile(null, context));
    }

    @Test
    public void testAjaxRequest() {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create().addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        final RequiresHttpAction e = (RequiresHttpAction) TestsHelper.expectException(() -> client.redirect(context));
        assertEquals(401, e.getCode());
        assertEquals(401, context.getResponseStatus());
    }

    @Test
    public void testAlreadyTried() {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.setSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        final RequiresHttpAction e = (RequiresHttpAction) TestsHelper.expectException(() -> client.redirect(context));
        assertEquals(401, e.getCode());
        assertEquals(401, context.getResponseStatus());
    }

    @Test
    public void testSaveAlreadyTried() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.getCredentials(context);
        assertEquals("true",
                (String) context.getSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX));
    }

    @Test
    public void testStateParameter() {
        final MockBaseClient<Credentials> client = new MockBaseClient<>(TYPE);
        final MockWebContext context = MockWebContext.create();
        TestsHelper.expectException(() -> client.redirect(context));
    }
}
