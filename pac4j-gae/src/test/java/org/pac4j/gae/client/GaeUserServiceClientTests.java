package org.pac4j.gae.client;

import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Tests the {@link GaeUserServiceClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class GaeUserServiceClientTests implements TestsConstants {

    private final static Logger logger = LoggerFactory.getLogger(GaeUserServiceClientTests.class);

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalUserServiceTestConfig())
                    .setEnvIsAdmin(true).setEnvIsLoggedIn(true).setEnvEmail(EMAIL).setEnvAuthDomain("");

    private GaeUserServiceClient client;

    private MockWebContext context;

    @Before
    public void setUp() {
        client = new GaeUserServiceClient();
        client.setCallbackUrl(CALLBACK_URL);
        context = MockWebContext.create();
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test(expected = TechnicalException.class)
    public void testCallbackMandatory() {
        final GaeUserServiceClient localClient = new GaeUserServiceClient();
        localClient.getRedirectionAction(context, new MockSessionStore());
    }

    @Test
    public void testRedirect() {
        final HttpAction action = client.getRedirectionAction(context, new MockSessionStore()).get();
        assertEquals(HttpConstants.FOUND, action.getCode());
        assertEquals("/_ah/login?continue=" + CommonHelper.urlEncode(CALLBACK_URL + "?" +
            Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=" + client.getName()), ((FoundAction) action).getLocation());
    }

    @Test
    public void testGetCredentialsUserProfile() {
        final GaeUserCredentials credentials = (GaeUserCredentials) client.getCredentials(context, new MockSessionStore()).get();
        final User user = credentials.getUser();
        assertEquals(EMAIL, user.getEmail());
        assertEquals("", user.getAuthDomain());
        final GaeUserServiceProfile profile =
            (GaeUserServiceProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        logger.debug("userProfile: {}", profile);
        assertEquals(EMAIL, profile.getId());
        assertEquals(GaeUserServiceProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + EMAIL, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GaeUserServiceProfile.class));
        assertEquals("test", profile.getDisplayName());
        assertTrue(profile.getRoles().contains("GLOBAL_ADMIN"));
        assertEquals(2, profile.getAttributes().size());
    }
}
