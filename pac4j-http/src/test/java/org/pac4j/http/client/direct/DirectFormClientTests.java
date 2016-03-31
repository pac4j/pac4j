package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DirectFormClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.6
 */
public final class DirectFormClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final DirectFormClient formClient = new DirectFormClient(null);
        TestsHelper.initShouldFail(formClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectFormClient formClient = new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.initShouldFail(formClient, "profileCreator cannot be null");
    }

    @Test
    public void testBadAuthenticatorType() {
        final DirectFormClient formClient = new DirectFormClient(new SimpleTestTokenAuthenticator());
        TestsHelper.initShouldFail(formClient, "Unsupported authenticator type: class org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectFormClient formClient = new DirectFormClient(new LocalCachingAuthenticator<>(new SimpleTestUsernamePasswordAuthenticator(), 10, 10, TimeUnit.DAYS));
        formClient.init(null);
    }

    private DirectFormClient getFormClient() {
        return new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator());
    }

    @Test
    public void testGetCredentialsMissingUsername() throws RequiresHttpAction {
        final DirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        assertNull(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)));
    }

    @Test
    public void testGetCredentialsMissingPassword() throws RequiresHttpAction {
        final DirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        assertNull(formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD)));
    }

    @Test
    public void testGetBadCredentials() throws RequiresHttpAction {
        final DirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create().addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), PASSWORD);
        try {
            formClient.getCredentials(context);
            fail();
        } catch (CredentialsException e) {
            assertEquals("Username : '" + USERNAME + "' does not match password", e.getMessage());
        }
    }

    @Test
    public void testGetGoodCredentials() throws RequiresHttpAction {
        final DirectFormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME));
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() throws RequiresHttpAction {
        final DirectFormClient formClient = getFormClient();
        formClient.setProfileCreator(credentials -> {
            String username = credentials.getUsername();
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return profile;
        });
        final MockWebContext context = MockWebContext.create();
        final CommonProfile profile = formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME,
                formClient.getName()), context);
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + UserProfile.SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
