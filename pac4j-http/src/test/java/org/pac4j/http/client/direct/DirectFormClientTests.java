package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.util.Optional;
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
        final var formClient = new DirectFormClient(null);
        TestsHelper.expectException(() -> formClient.getCredentials(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final var formClient = new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.expectException(() -> formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create(), new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final var formClient
            = new DirectFormClient(new LocalCachingAuthenticator(new SimpleTestUsernamePasswordAuthenticator(), 10, 10, TimeUnit.DAYS));
        formClient.init();
    }

    private DirectFormClient getFormClient() {
        return new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final var formClient = getFormClient();
        final var context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME),
            new MockSessionStore()).isPresent());
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final var formClient = getFormClient();
        final var context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD),
            new MockSessionStore()).isPresent());
    }

    @Test
    public void testGetBadCredentials() {
        final var formClient = getFormClient();
        final var context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), PASSWORD), new MockSessionStore()).isPresent());
    }

    @Test
    public void testGetGoodCredentials() {
        final var formClient = getFormClient();
        final var credentials = (UsernamePasswordCredentials) formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME), new MockSessionStore()).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final var formClient = getFormClient();
        formClient.setProfileCreator((credentials, context, session) -> {
            var username = ((UsernamePasswordCredentials) credentials).getUsername();
            final var profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return Optional.of(profile);
        });
        final var context = MockWebContext.create();
        final var profile = (CommonProfile) formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME),
            context, new MockSessionStore()).get();
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
