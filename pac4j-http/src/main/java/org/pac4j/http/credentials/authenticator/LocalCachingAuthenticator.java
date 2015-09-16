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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.http.credentials.HttpCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * An authenticator that caches the result of an authentication event locally.
 * The authentication is delegated to a pluggable component.
 * @author Misagh Moayyed
 * @since 1.8
 */
public class LocalCachingAuthenticator<T extends HttpCredentials> implements Authenticator<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final LoadingCache<T, UserProfile> cache;
    private final UserProfileCacheLoader<T> cacheLoader;

    public LocalCachingAuthenticator(final Authenticator<T> delegate,
                                     final long cacheSize, final long timeout,
                                     final TimeUnit timeUnit) {

        this.cacheLoader = new UserProfileCacheLoader<>(delegate);
        this.cache = CacheBuilder.newBuilder().maximumSize(cacheSize)
                .expireAfterWrite(timeout, timeUnit).build(this.cacheLoader);
    }

    @Override
    public void validate(final T credentials) {
        try {
            final UserProfile profile = this.cache.get(credentials);
            credentials.setUserProfile(profile);
            logger.debug("Found cached credential. Using cached profile {}...", profile);
        } catch (Exception e) {
            throw new CredentialsException(e);
        }

    }

    public void setDelegate(final Authenticator<T> delegate) {
        this.cacheLoader.setDelegate(delegate);
    }

    public void removeFromCache(final T credentials) {
        this.cache.invalidate(credentials);
    }

    public boolean isCached(final T credentials) {
        return this.cache.getIfPresent(credentials) != null;
    }

    public boolean clearCache() {
        this.cache.invalidateAll();
        return this.cache.asMap().isEmpty();
    }

    private static class UserProfileCacheLoader<T extends HttpCredentials> extends CacheLoader<T, UserProfile> {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private Authenticator<T> delegate;

        public UserProfileCacheLoader(final Authenticator<T> delegate) {
            this.delegate = delegate;
        }

        public UserProfile load(final T credentials) throws Exception {
            logger.debug("Delegating authentication to {}...", delegate);
            delegate.validate(credentials);
            final UserProfile profile = credentials.getUserProfile();
            logger.debug("Cached authentication result");
            return profile;
        }

        public void setDelegate(final Authenticator<T> delegate) {
            this.delegate = delegate;
        }
    }
}
