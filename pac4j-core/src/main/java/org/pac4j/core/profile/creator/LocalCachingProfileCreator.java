package org.pac4j.core.profile.creator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A profile creator that caches the result of a profile creation based on the credentials.
 *
 * Add the <code>guava</code> dependency to use this class.
 *
 * @author Jerome LELEU
 * @since 5.7.0
 */
public class LocalCachingProfileCreator extends InitializableObject implements ProfileCreator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ProfileCreator delegate;
    private int cacheSize;
    private int timeout;
    private TimeUnit timeUnit;

    private Store<Credentials, UserProfile> store;

    public LocalCachingProfileCreator() {}

    public LocalCachingProfileCreator(final ProfileCreator delegate, final Store<Credentials, UserProfile> store) {
        this.delegate = delegate;
        this.store = store;
    }

    public LocalCachingProfileCreator(final ProfileCreator delegate, final int cacheSize,
                                      final int timeout, final TimeUnit timeUnit) {
        this.delegate = delegate;
        this.cacheSize = cacheSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public Optional<UserProfile> create(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
        init();

        var optProfile = this.store.get(credentials);
        if (optProfile.isEmpty()) {
            logger.debug("No cached credentials found. Delegating profile creation to {}...", delegate);
            final Optional<UserProfile> profile = delegate.create(credentials, context, sessionStore);
            if (profile.isPresent()) {
                logger.debug("Caching credential. Using profile {}...", profile.get());
                store.set(credentials, profile.get());
            }
            return profile;
        } else {
            logger.debug("Found cached credential. Using cached profile {}...", optProfile.get());
            return optProfile;
        }
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        if (this.store == null) {
            this.store = new GuavaStore<>(cacheSize, timeout, timeUnit);
        }

        if (delegate instanceof InitializableObject) {
            ((InitializableObject) delegate).init(forceReinit);
        }
    }

    public void removeFromCache(final Credentials credentials) {
        this.store.remove(credentials);
    }

    public boolean isCached(final Credentials credentials) {
        return this.store.get(credentials).isPresent();
    }

    public ProfileCreator getDelegate() {
        return delegate;
    }

    public void setDelegate(final ProfileCreator delegate) {
        this.delegate = delegate;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Store<Credentials, UserProfile> getStore() {
        return store;
    }

    public void setStore(final Store<Credentials, UserProfile> store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "delegate", this.delegate, "store", this.store);
    }
}
