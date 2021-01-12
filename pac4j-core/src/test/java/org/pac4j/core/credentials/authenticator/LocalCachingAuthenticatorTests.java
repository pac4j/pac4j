package org.pac4j.core.credentials.authenticator;

import org.junit.Test;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

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
        public void validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
            if (n > 0) {
                throw new IllegalArgumentException("Cannot call validate twice");
            }
            credentials.setUserProfile(new CommonProfile());
            n++;
        }
    }

    private static class SimpleUPAuthenticator implements Authenticator {

        @Override
        public void validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
            final CommonProfile profile = new CommonProfile();
            profile.setId(((UsernamePasswordCredentials) credentials).getUsername());
            credentials.setUserProfile(profile);
        }
    }

    private final Authenticator delegate = new SimpleUPAuthenticator();

    private final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("a", "a");

    @Test
    public void testDoubleCalls() {
        final OnlyOneCallAuthenticator authenticator = new OnlyOneCallAuthenticator();
        final LocalCachingAuthenticator localCachingAuthenticator = new LocalCachingAuthenticator(authenticator, 10, 10, TimeUnit.SECONDS);
        localCachingAuthenticator.init();
        final Credentials credentials1 = new UsernamePasswordCredentials("a", "a");
        localCachingAuthenticator.validate(credentials1, null, null);
        final Credentials credentials2 = new UsernamePasswordCredentials("a", "a");
        localCachingAuthenticator.validate(credentials2, null, null);
    }

    @Test
    public void testNoCache() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCache() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        authenticator.validate(this.credentials, null, null);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheSwitchDelegate() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        authenticator.validate(this.credentials, null, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.validate(this.credentials, null, null);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test(expected=CredentialsException.class)
    public void testValidateAndNoCacheSwitchDelegate() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.MINUTES);
        authenticator.init();
        authenticator.validate(this.credentials, null, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.removeFromCache(this.credentials);
        authenticator.validate(this.credentials, null, null);
    }

    @Test
    public void testValidateAndCacheAndRemove() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init();

        authenticator.validate(this.credentials, null, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.removeFromCache(this.credentials);
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndExpire() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 500, TimeUnit.MILLISECONDS);
        authenticator.init();

        authenticator.validate(this.credentials, null, null);
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
        public void validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
            throw new CredentialsException("fail");
        }
    }
}
