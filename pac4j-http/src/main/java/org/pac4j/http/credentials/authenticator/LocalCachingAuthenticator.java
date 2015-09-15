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

import org.pac4j.core.profile.UserProfile;
import org.pac4j.http.credentials.HttpCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * An authenticator that caches the result of an authentication event locally.
 * The authentication is delegated to a pluggable component.
 * @author Misagh Moayyed
 * @since 1.8
 */
public class LocalCachingAuthenticator<T extends HttpCredentials> implements Authenticator<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<T, UserProfile> cache;

    private final Authenticator<T> delegate;

    public LocalCachingAuthenticator(final Authenticator<T> delegate) {
        this.delegate = delegate;
        this.cache = new HashMap<>();
    }

    @Override
    public void validate(final T credentials) {
        if (isCached(credentials)) {
            final UserProfile profile = this.cache.get(credentials);
            credentials.setUserProfile(profile);
            logger.debug("Found cached credential. Using cached profile {}...", profile);
        } else {
            logger.debug("Delegating authentication to {}...", this.delegate);
            this.delegate.validate(credentials);
            final UserProfile profile = credentials.getUserProfile();
            this.cache.put(credentials, profile);
            logger.debug("Cached authentication result");
        }
    }

    public UserProfile removeFromCache(final T credentials) {
        return this.cache.remove(credentials);
    }

    public boolean isCached(final T credentials) {
        return this.cache.containsKey(credentials);
    }

    public void clearCache() {
        this.cache.clear();
    }
}
