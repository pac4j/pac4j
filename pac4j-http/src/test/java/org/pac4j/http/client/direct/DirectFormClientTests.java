package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
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
        val formClient = new DirectFormClient(null);
        TestsHelper.expectException(() -> formClient.getCredentials(MockWebContext.create(), new MockSessionStore(),
                ProfileManagerFactory.DEFAULT), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val formClient = new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.expectException(() -> formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create(), new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val formClient
            = new DirectFormClient(new LocalCachingAuthenticator(new SimpleTestUsernamePasswordAuthenticator(), 10, 10, TimeUnit.DAYS));
        formClient.init();
    }

    private DirectFormClient getFormClient() {
        return new DirectFormClient(new SimpleTestUsernamePasswordAuthenticator());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        val formClient = getFormClient();
        val context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME),
            new MockSessionStore(), ProfileManagerFactory.DEFAULT).isPresent());
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        val formClient = getFormClient();
        val context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD),
            new MockSessionStore(), ProfileManagerFactory.DEFAULT).isPresent());
    }

    @Test
    public void testGetBadCredentials() {
        val formClient = getFormClient();
        val context = MockWebContext.create();
        assertFalse(formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), PASSWORD), new MockSessionStore(),
                ProfileManagerFactory.DEFAULT).isPresent());
    }

    @Test
    public void testGetGoodCredentials() {
        val formClient = getFormClient();
        val credentials = (UsernamePasswordCredentials) formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME), new MockSessionStore(),
                ProfileManagerFactory.DEFAULT).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        val formClient = getFormClient();
        formClient.setProfileCreator((credentials, context, session) -> {
            var username = ((UsernamePasswordCredentials) credentials).getUsername();
            val profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return Optional.of(profile);
        });
        val context = MockWebContext.create();
        val profile = (CommonProfile) formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME),
            context, new MockSessionStore()).get();
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
