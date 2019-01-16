package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
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
        TestsHelper.expectException(() -> formClient.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectFormClient formClient = new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.expectException(() -> formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectFormClient formClient
            = new DirectFormClient(new LocalCachingAuthenticator<>(new SimpleTestUsernamePasswordAuthenticator(), 10, 10, TimeUnit.DAYS));
        formClient.init();
    }

    private DirectFormClient getFormClient() {
        return new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final DirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)).isPresent());
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final DirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD)).isPresent());
    }

    @Test
    public void testGetBadCredentials() {
        final DirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), PASSWORD)).isPresent());
    }

    @Test
    public void testGetGoodCredentials() {
        final DirectFormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME)).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final DirectFormClient formClient = getFormClient();
        formClient.setProfileCreator((credentials, context) -> {
            String username = credentials.getUsername();
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return profile;
        });
        final MockWebContext context = MockWebContext.create();
        final CommonProfile profile =
            (CommonProfile) formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME), context);
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + CommonProfile.SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
