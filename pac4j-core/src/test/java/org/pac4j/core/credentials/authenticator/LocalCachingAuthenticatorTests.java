package org.pac4j.core.credentials.authenticator;

import org.junit.Test;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link LocalCachingAuthenticator}
 * @author Misagh Moayyed
 * @since 1.8
 */
@SuppressWarnings("unchecked")
public class LocalCachingAuthenticatorTests {

    private static class OnlyOneCallAuthenticator implements UsernamePasswordAuthenticator {

        private int n = 0;

        @Override
        public void validate(final UsernamePasswordCredentials credentials) {
            if (n > 0) {
                throw new IllegalArgumentException("Cannot call validate twice");
            }
            credentials.setUserProfile(new CommonProfile());
            n++;
        }
    }

    private static class SimpleUPAuthenticator implements UsernamePasswordAuthenticator {

        @Override
        public void validate(final UsernamePasswordCredentials credentials) {
            final CommonProfile profile = new CommonProfile();
            profile.setId(credentials.getUsername());
            credentials.setUserProfile(profile);
        }
    }

    private final Authenticator delegate = new SimpleUPAuthenticator();

    private final Credentials credentials =
            new UsernamePasswordCredentials("a", "a", this.getClass().getName());

    @Test
    public void testDoubleCalls() throws RequiresHttpAction {
        final OnlyOneCallAuthenticator authenticator = new OnlyOneCallAuthenticator();
        final LocalCachingAuthenticator localCachingAuthenticator = new LocalCachingAuthenticator(authenticator, 10, 10, TimeUnit.SECONDS);
        localCachingAuthenticator.init(null);
        final Credentials credentials1 = new UsernamePasswordCredentials("a", "a", this.getClass().getName());
        localCachingAuthenticator.validate(credentials1);
        final Credentials credentials2 = new UsernamePasswordCredentials("a", "a", this.getClass().getName());
        localCachingAuthenticator.validate(credentials2);
    }

    @Test
    public void testNoCache() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCache() throws RequiresHttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheSwitchDelegate() throws RequiresHttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test(expected=CredentialsException.class)
    public void testValidateAndNoCacheSwitchDelegate() throws Exception {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.MINUTES);
        authenticator.init(null);
        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.removeFromCache(this.credentials);
        authenticator.validate(this.credentials);
    }

    @Test
    public void testValidateAndCacheAndRemove() throws RequiresHttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.removeFromCache(this.credentials);
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheAndClean() throws RequiresHttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.clearCache();
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndExpire() throws Exception {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 500, TimeUnit.MILLISECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        Thread.sleep(600);
        assertFalse(authenticator.isCached(this.credentials));
    }

    private static class ThrowingAuthenticator implements UsernamePasswordAuthenticator {

        @Override
        public void validate(final UsernamePasswordCredentials credentials) {
            throw new CredentialsException("fail");
        }
    }
}
