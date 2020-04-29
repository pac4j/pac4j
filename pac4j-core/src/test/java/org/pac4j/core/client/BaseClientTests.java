package org.pac4j.core.client;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;

import static org.junit.Assert.*;

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
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) client.getRedirectionAction(context).get();
        final String redirectionUrl = action.getLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        final Optional<Credentials> credentials = client.getCredentials(context);
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testIndirectClientWithImmediate() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) client.getRedirectionAction(context).get();
        final String redirectionUrl = action.getLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    @Test
    public void testNullCredentials() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockWebContext context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertFalse(client.getUserProfile(null, context).isPresent());
    }

    @Test
    public void testNullCredentialsButForceAnonymous() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setForceAnonymousProfileWhenNotAuthenticated(true);
        client.setForceAnonymousProfileWhenNotAuthenticated(true);
        final MockWebContext context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertEquals(AnonymousProfile.INSTANCE, client.getUserProfile(null, context).get());
    }

    @Test
    public void testAjaxRequest() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create()
                                        .addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        final HttpAction e = (HttpAction) TestsHelper.expectException(() -> client.getRedirectionAction(context));
        assertEquals(401, e.getCode());
    }

    @Test
    public void testAlreadyTried() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        final HttpAction e = (HttpAction) TestsHelper.expectException(() -> client.getRedirectionAction(context));
        assertEquals(401, e.getCode());
    }

    @Test
    public void testSaveAlreadyTried() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.getCredentials(context);
        assertEquals("true", context.getSessionStore()
            .get(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX).get());
    }

    @Test
    public void testStateParameter() {
        final MockIndirectClient client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockWebContext context = MockWebContext.create();
        TestsHelper.expectException(() -> client.getRedirectionAction(context));
    }
}
