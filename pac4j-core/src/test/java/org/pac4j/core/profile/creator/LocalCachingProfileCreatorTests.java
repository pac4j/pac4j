package org.pac4j.core.profile.creator;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests {@link LocalCachingProfileCreator}.
 *
 * @author Jerome LELEU
 * @since 5.7.0
 */
public class LocalCachingProfileCreatorTests {

    private static class SimpleProfileCreator implements ProfileCreator {

        private static final SimpleProfileCreator INSTANCE = new SimpleProfileCreator();

        private static int generator = 0;

        @Override
        public Optional<UserProfile> create(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
            final CommonProfile profile = new CommonProfile();
            profile.setId("" + generator++);
            return Optional.of(profile);
        }
    }

    private static class NoProfileCreator implements ProfileCreator {

        private static final NoProfileCreator INSTANCE = new NoProfileCreator();

        @Override
        public Optional<UserProfile> create(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
            return Optional.empty();
        }
    }

    @Test
    public void testCachingNoProfile() {
        final var c1 = new TokenCredentials("T1");
        final var pc = new LocalCachingProfileCreator(NoProfileCreator.INSTANCE, 10, 10, TimeUnit.SECONDS);
        final var optProfile = pc.create(c1, MockWebContext.create(), new MockSessionStore());
        assertTrue(optProfile.isEmpty());
        assertTrue(pc.getStore().get(c1).isEmpty());
    }

    @Test
    public void testCachingProfile() {
        final var c1 = new TokenCredentials("T1");
        final var pc = new LocalCachingProfileCreator(SimpleProfileCreator.INSTANCE, 10, 1, TimeUnit.SECONDS);
        final var optProfile = pc.create(c1, MockWebContext.create(), new MockSessionStore());
        assertTrue(optProfile.isPresent());
        final var profileId = optProfile.get().getId();
        assertEquals(profileId, pc.getStore().get(c1).get().getId());

        final var optProfile2 = pc.create(c1, MockWebContext.create(), new MockSessionStore());
        assertTrue(optProfile2.isPresent());
        final var profileId2 = optProfile2.get().getId();
        assertEquals(profileId, pc.getStore().get(c1).get().getId());
        assertEquals(profileId, profileId2);

        TestsHelper.wait(1500);

        final var optProfile3 = pc.create(c1, MockWebContext.create(), new MockSessionStore());
        assertTrue(optProfile3.isPresent());
        final var profileId3 = optProfile3.get().getId();
        assertEquals(profileId3, pc.getStore().get(c1).get().getId());
        assertNotEquals(profileId3, profileId2);
    }
}
