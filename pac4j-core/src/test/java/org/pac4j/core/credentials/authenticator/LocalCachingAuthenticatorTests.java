package org.pac4j.core.credentials.authenticator;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link LocalCachingAuthenticator}.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
@SuppressWarnings("unchecked")
public class LocalCachingAuthenticatorTests {

    private static class OnlyOneCallAuthenticator implements Authenticator {

        private int n = 0;

        @Override
        public Optional<Credentials> validate(final CallContext ctx, final Credentials credentials) {
            if (n > 0) {
                throw new IllegalArgumentException("Cannot call validate twice");
            }
            credentials.setUserProfile(new CommonProfile());
            n++;
            return Optional.of(credentials);
        }
    }

    private static class SimpleUPAuthenticator implements Authenticator {

        @Override
        public Optional<Credentials> validate(final CallContext ctx, final Credentials credentials) {
            val profile = new CommonProfile();
            profile.setId(((UsernamePasswordCredentials) credentials).getUsername());
            credentials.setUserProfile(profile);
            return Optional.of(credentials);
        }
    }

    private final Authenticator delegate = new SimpleUPAuthenticator();

    private final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("a", "a");

    @Test
    public void testDoubleCalls() {
        val authenticator = new OnlyOneCallAuthenticator();
        val localCachingAuthenticator = new LocalCachingAuthenticator(authenticator, 10, 10, TimeUnit.SECONDS);
        localCachingAuthenticator.init();
        final Credentials credentials1 = new UsernamePasswordCredentials("a", "a");
        localCachingAuthenticator.validate(null, credentials1);
        final Credentials credentials2 = new UsernamePasswordCredentials("a", "a");
        localCachingAuthenticator.validate(null, credentials2);
    }

    @Test
    public void testNoCache() {
        val authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCache() {
        val authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        authenticator.validate(null, this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheSwitchDelegate() {
        val authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        authenticator.validate(null, this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.validate(null, this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test(expected=CredentialsException.class)
    public void testValidateAndNoCacheSwitchDelegate() {
        val authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.MINUTES);
        authenticator.init();
        authenticator.validate(null, this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.removeFromCache(this.credentials);
        authenticator.validate(null, this.credentials);
    }

    @Test
    public void testValidateAndCacheAndRemove() {
        val authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        authenticator.validate(null, this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.removeFromCache(this.credentials);
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndExpire() {
        val authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 500, TimeUnit.MILLISECONDS);
        authenticator.init();

        authenticator.validate(null, this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        try {
            Thread.sleep(600);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertFalse(authenticator.isCached(this.credentials));
    }

    private static class ThrowingAuthenticator implements Authenticator {

        @Override
        public Optional<Credentials> validate(final CallContext ctx, Credentials credentials) {
            throw new CredentialsException("fail");
        }
    }
}
