package org.pac4j.core.credentials.authenticator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * An authenticator that caches the result of an authentication event locally.
 * The authentication is delegated to a pluggable component.
 *
 * Add the <code>guava</code> dependency to use this class.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public class LocalCachingAuthenticator<T extends Credentials> extends InitializableWebObject implements Authenticator<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Authenticator<T> delegate;
    private long cacheSize;
    private long timeout;
    private TimeUnit timeUnit;

    private LoadingCache<T, CommonProfile> cache;
    private UserProfileCacheLoader<T> cacheLoader;

    public LocalCachingAuthenticator() {}

    public LocalCachingAuthenticator(final Authenticator<T> delegate, final long cacheSize,
                                     final long timeout, final TimeUnit timeUnit) {
        this.delegate = delegate;
        this.cacheSize = cacheSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void validate(final T credentials) throws HttpAction {
        try {
            final CommonProfile profile = this.cache.get(credentials);
            credentials.setUserProfile(profile);
            logger.debug("Found cached credential. Using cached profile {}...", profile);
        } catch (Exception e) {
            throw new CredentialsException(e);
        }

    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("delegate", this.delegate);
        CommonHelper.assertTrue(cacheSize > 0, "cacheSize must be > 0");
        CommonHelper.assertTrue(timeout > 0, "timeout must be > 0");
        CommonHelper.assertNotNull("timeUnit", this.timeUnit);

        if (delegate instanceof InitializableWebObject) {
            ((InitializableWebObject) delegate).init(context);
        }

        this.cacheLoader = new UserProfileCacheLoader<>(delegate);
        this.cache = CacheBuilder.newBuilder().maximumSize(cacheSize)
                .expireAfterWrite(timeout, timeUnit).build(this.cacheLoader);
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

    public Authenticator<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(Authenticator<T> delegate) {
        this.delegate = delegate;
        if (this.cacheLoader != null) {
            this.cacheLoader.setDelegate(delegate);
        }
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "delegate", this.delegate, "cacheSize", this.cacheSize,
                "timeout", this.timeout, "timeUnit", this.timeUnit);
    }

    private static class UserProfileCacheLoader<T extends Credentials> extends CacheLoader<T, CommonProfile> {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private Authenticator<T> delegate;

        public UserProfileCacheLoader(final Authenticator<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public CommonProfile load(final T credentials) throws Exception {
            logger.debug("Delegating authentication to {}...", delegate);
            delegate.validate(credentials);
            final CommonProfile profile = credentials.getUserProfile();
            logger.debug("Cached authentication result");
            return profile;
        }

        public Authenticator<T> getDelegate() {
            return delegate;
        }

        public void setDelegate(final Authenticator<T> delegate) {
            this.delegate = delegate;
        }
    }
}
