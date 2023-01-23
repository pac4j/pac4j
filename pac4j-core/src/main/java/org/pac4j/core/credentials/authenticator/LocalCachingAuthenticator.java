package org.pac4j.core.credentials.authenticator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.InitializableObject;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * An authenticator that caches the result of an authentication based on the credentials.
 *
 * Add the <code>guava</code> dependency to use this class.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
@Getter
@Setter
@Slf4j
@ToString
public class LocalCachingAuthenticator extends InitializableObject implements Authenticator {

    private Authenticator delegate;
    private int cacheSize;
    private int timeout;
    private TimeUnit timeUnit;

    private Store<AuthenticationCredentials, UserProfile> store;

    public LocalCachingAuthenticator() {}

    public LocalCachingAuthenticator(final Authenticator delegate, final Store<AuthenticationCredentials, UserProfile> store) {
        this.delegate = delegate;
        this.store = store;
    }

    public LocalCachingAuthenticator(final Authenticator delegate, final int cacheSize,
                                     final int timeout, final TimeUnit timeUnit) {
        this.delegate = delegate;
        this.cacheSize = cacheSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public Optional<AuthenticationCredentials> validate(final CallContext ctx, final AuthenticationCredentials credentials) {
        init();

        var optProfile = this.store.get(credentials);
        if (optProfile.isEmpty()) {
            LOGGER.debug("No cached credentials found. Delegating authentication to {}...", delegate);
            delegate.validate(ctx, credentials);
            val profile = credentials.getUserProfile();
            LOGGER.debug("Caching credential. Using profile {}...", profile);
            store.set(credentials, profile);
        } else {
            credentials.setUserProfile(optProfile.get());
            LOGGER.debug("Found cached credential. Using cached profile {}...", optProfile.get());
        }

        return Optional.of(credentials);
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

    public void removeFromCache(final AuthenticationCredentials credentials) {
        this.store.remove(credentials);
    }

    public boolean isCached(final AuthenticationCredentials credentials) {
        return this.store.get(credentials).isPresent();
    }
}
