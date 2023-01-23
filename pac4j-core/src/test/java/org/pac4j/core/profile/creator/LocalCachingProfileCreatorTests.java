package org.pac4j.core.profile.creator;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
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
        public Optional<UserProfile> create(final CallContext ctx, Credentials credentials) {
            final CommonProfile profile = new CommonProfile();
            profile.setId("" + generator++);
            return Optional.of(profile);
        }
    }

    private static class NoProfileCreator implements ProfileCreator {

        private static final NoProfileCreator INSTANCE = new NoProfileCreator();

        @Override
        public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
            return Optional.empty();
        }
    }

    @Test
    public void testCachingNoProfile() {
        val c1 = new TokenCredentials("T1");
        val pc = new LocalCachingProfileCreator(NoProfileCreator.INSTANCE, 10, 10, TimeUnit.SECONDS);
        val optProfile = pc.create(new CallContext(MockWebContext.create(), new MockSessionStore()), c1);
        assertTrue(optProfile.isEmpty());
        assertTrue(pc.getStore().get(c1).isEmpty());
    }

    @Test
    public void testCachingProfile() {
        val c1 = new TokenCredentials("T1");
        val pc = new LocalCachingProfileCreator(SimpleProfileCreator.INSTANCE, 10, 1, TimeUnit.SECONDS);
        val optProfile = pc.create(new CallContext(MockWebContext.create(), new MockSessionStore()), c1);
        assertTrue(optProfile.isPresent());
        val profileId = optProfile.get().getId();
        assertEquals(profileId, pc.getStore().get(c1).get().getId());

        val optProfile2 = pc.create(new CallContext(MockWebContext.create(), new MockSessionStore()), c1);
        assertTrue(optProfile2.isPresent());
        val profileId2 = optProfile2.get().getId();
        assertEquals(profileId, pc.getStore().get(c1).get().getId());
        assertEquals(profileId, profileId2);

        TestsHelper.wait(1500);

        val optProfile3 = pc.create(new CallContext(MockWebContext.create(), new MockSessionStore()), c1);
        assertTrue(optProfile3.isPresent());
        val profileId3 = optProfile3.get().getId();
        assertEquals(profileId3, pc.getStore().get(c1).get().getId());
        assertNotEquals(profileId3, profileId2);
    }
}
