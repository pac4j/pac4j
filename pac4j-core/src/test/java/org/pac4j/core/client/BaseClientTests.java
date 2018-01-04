package org.pac4j.core.client;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests the {@link BaseClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class BaseClientTests implements TestsConstants {

    @Test
    public void testDirectClient() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        boolean hasCredentials = client.getCredentials(context).isPresent();
        assertFalse(hasCredentials);
    }

    @Test
    public void testIndirectClientWithImmediate() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    @Test
    public void testNullCredentials() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockWebContext context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertFalse(client.getUserProfile(null, context).isPresent());
    }

    @Test
    public void testAjaxRequest() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create()
            .addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        final HttpAction e = (HttpAction) TestsHelper.expectException(() -> client.redirect(context));
        assertEquals(401, e.getCode());
        assertEquals(401, context.getResponseStatus());
    }

    @Test
    public void testAlreadyTried() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        final HttpAction e = (HttpAction) TestsHelper.expectException(() -> client.redirect(context));
        assertEquals(401, e.getCode());
        assertEquals(401, context.getResponseStatus());
    }

    @Test
    public void testSaveAlreadyTried() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.getCredentials(context);
        assertEquals(
            "true", context.getSessionStore().get(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX).get()
        );
    }

    @Test
    public void testStateParameter() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockWebContext context = MockWebContext.create();
        TestsHelper.expectException(() -> client.redirect(context));
    }
}
