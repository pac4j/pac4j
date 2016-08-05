package org.pac4j.core.credentials.authenticator;

import org.junit.Test;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
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

    private static class OnlyOneCallAuthenticator implements Authenticator<UsernamePasswordCredentials> {

        private int n = 0;

        @Override
        public void validate(final UsernamePasswordCredentials credentials, final WebContext context) {
            if (n > 0) {
                throw new IllegalArgumentException("Cannot call validate twice");
            }
            credentials.setUserProfile(new CommonProfile());
            n++;
        }
    }

    private static class SimpleUPAuthenticator implements Authenticator<UsernamePasswordCredentials> {

        @Override
        public void validate(final UsernamePasswordCredentials credentials, final WebContext context) {
            final CommonProfile profile = new CommonProfile();
            profile.setId(credentials.getUsername());
            credentials.setUserProfile(profile);
        }
    }

    private final Authenticator delegate = new SimpleUPAuthenticator();

    private final UsernamePasswordCredentials credentials =
            new UsernamePasswordCredentials("a", "a", this.getClass().getName());

    @Test
    public void testDoubleCalls() throws HttpAction {
        final OnlyOneCallAuthenticator authenticator = new OnlyOneCallAuthenticator();
        final LocalCachingAuthenticator localCachingAuthenticator = new LocalCachingAuthenticator(authenticator, 10, 10, TimeUnit.SECONDS);
        localCachingAuthenticator.init(null);
        final Credentials credentials1 = new UsernamePasswordCredentials("a", "a", this.getClass().getName());
        localCachingAuthenticator.validate(credentials1, null);
        final Credentials credentials2 = new UsernamePasswordCredentials("a", "a", this.getClass().getName());
        localCachingAuthenticator.validate(credentials2, null);
    }

    @Test
    public void testNoCache() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCache() throws HttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheSwitchDelegate() throws HttpAction {
        final LocalCachingAuthenticator<UsernamePasswordCredentials> authenticator = new
                LocalCachingAuthenticator<>(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test(expected=CredentialsException.class)
    public void testValidateAndNoCacheSwitchDelegate() throws Exception {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.MINUTES);
        authenticator.init(null);
        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.removeFromCache(this.credentials);
        authenticator.validate(this.credentials, null);
    }

    @Test
    public void testValidateAndCacheAndRemove() throws HttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.removeFromCache(this.credentials);
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheAndClean() throws HttpAction {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.clearCache();
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndExpire() throws Exception {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 500, TimeUnit.MILLISECONDS);
        authenticator.init(null);

        authenticator.validate(this.credentials, null);
        assertTrue(authenticator.isCached(this.credentials));
        Thread.sleep(600);
        assertFalse(authenticator.isCached(this.credentials));
    }

    private static class ThrowingAuthenticator implements Authenticator<UsernamePasswordCredentials> {

        @Override
        public void validate(final UsernamePasswordCredentials credentials, final WebContext context) {
            throw new CredentialsException("fail");
        }
    }
}
