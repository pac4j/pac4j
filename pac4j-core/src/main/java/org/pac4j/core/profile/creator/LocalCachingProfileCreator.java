package org.pac4j.core.profile.creator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.InitializableObject;

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
@Getter
@Setter
@ToString(callSuper = true)
@Slf4j
public class LocalCachingProfileCreator extends InitializableObject implements ProfileCreator {

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
    public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
        init();

        val optProfile = this.store.get(credentials);
        if (optProfile.isEmpty()) {
            LOGGER.debug("No cached credentials found. Delegating profile creation to {}...", delegate);
            val profile = delegate.create(ctx, credentials);
            if (profile.isPresent()) {
                LOGGER.debug("Caching credential. Using profile {}...", profile.get());
                store.set(credentials, profile.get());
            }
            return profile;
        } else {
            LOGGER.debug("Found cached credential. Using cached profile {}...", optProfile.get());
            return optProfile;
        }
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        if (this.store == null) {
            this.store = new GuavaStore<>(cacheSize, timeout, timeUnit);
        }

        if (delegate instanceof InitializableObject initializableObject) {
            initializableObject.init(forceReinit);
        }
    }

    public void removeFromCache(final Credentials credentials) {
        this.store.remove(credentials);
    }

    public boolean isCached(final Credentials credentials) {
        return this.store.get(credentials).isPresent();
    }
}
