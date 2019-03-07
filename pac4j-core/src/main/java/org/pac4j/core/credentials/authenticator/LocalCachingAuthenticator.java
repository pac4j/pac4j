package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
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
public class LocalCachingAuthenticator<T extends Credentials> extends InitializableObject implements Authenticator<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Authenticator<T> delegate;
    private int cacheSize;
    private int timeout;
    private TimeUnit timeUnit;

    private Store<T, CommonProfile> store;

    public LocalCachingAuthenticator() {}

    public LocalCachingAuthenticator(final Authenticator<T> delegate, final Store<T, CommonProfile> store) {
        this.delegate = delegate;
        this.store = store;
    }

    public LocalCachingAuthenticator(final Authenticator<T> delegate, final int cacheSize,
                                     final int timeout, final TimeUnit timeUnit) {
        this.delegate = delegate;
        this.cacheSize = cacheSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void validate(final T credentials, final WebContext context) {
        init();

        Optional<CommonProfile> optProfile = this.store.get(credentials);
        if (!optProfile.isPresent()) {
            logger.debug("No cached credentials found. Delegating authentication to {}...", delegate);
            delegate.validate(credentials, context);
            final CommonProfile profile = credentials.getUserProfile();
            logger.debug("Caching credential. Using profile {}...", profile);
            store.set(credentials, profile);
        } else {
            credentials.setUserProfile(optProfile.get());
            logger.debug("Found cached credential. Using cached profile {}...", optProfile.get());
        }
    }

    @Override
    protected void internalInit() {
        if (this.store == null) {
            this.store = new GuavaStore<>(cacheSize, timeout, timeUnit);
        }

        if (delegate instanceof InitializableObject) {
            ((InitializableObject) delegate).init();
        }
    }


    public void removeFromCache(final T credentials) {
        this.store.remove(credentials);
    }

    public boolean isCached(final T credentials) {
        return this.store.get(credentials).isPresent();
    }

    public Authenticator<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(final Authenticator<T> delegate) {
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

    public Store<T, CommonProfile> getStore() {
        return store;
    }

    public void setStore(final Store<T, CommonProfile> store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "delegate", this.delegate, "store", this.store);
    }
}
