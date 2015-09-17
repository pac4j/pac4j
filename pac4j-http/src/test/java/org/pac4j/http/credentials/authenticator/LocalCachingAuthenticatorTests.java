/*
 *    Copyright 2012 - 2015 pac4j organization
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.pac4j.http.credentials.authenticator;

import org.junit.Test;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.HttpCredentials;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link LocalCachingAuthenticator}
 * @author Misagh Moayyed
 * @since 1.8
 */
public class LocalCachingAuthenticatorTests {
    private SimpleTestUsernamePasswordAuthenticator delegate =
            new SimpleTestUsernamePasswordAuthenticator();

    private final HttpCredentials credentials =
            new UsernamePasswordCredentials("a", "a", this.getClass().getName());

    @Test
    public void testNoCache() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);

        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCache() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheSwitchDelegate() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);

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
        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.setDelegate(new ThrowingAuthenticator());
        authenticator.removeFromCache(this.credentials);
        authenticator.validate(this.credentials);

    }

    @Test
    public void testValidateAndCacheAndRemove() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.removeFromCache(this.credentials);
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndCacheAndClean() {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 2, TimeUnit.SECONDS);

        authenticator.validate(this.credentials);
        assertTrue(authenticator.isCached(this.credentials));
        authenticator.clearCache();
        assertFalse(authenticator.isCached(this.credentials));
    }

    @Test
    public void testValidateAndExpire() throws Exception {
        final LocalCachingAuthenticator authenticator = new
                LocalCachingAuthenticator(this.delegate, 10, 500, TimeUnit.MILLISECONDS);

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
