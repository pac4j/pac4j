package org.pac4j.saml.replay;

import net.shibboleth.shared.component.ComponentInitializationException;
import org.opensaml.storage.ReplayCache;
import org.opensaml.storage.impl.MemoryStorageService;
import org.opensaml.storage.impl.StorageServiceReplayCache;
import org.pac4j.saml.exceptions.SAMLException;


/**
 * Default replay cache provider which stores the identifiers in memory. This
 * implementation will not work in a clustered environment and requires the same
 * instance is used for all SAML authentications.
 */
public class InMemoryReplayCacheProvider implements ReplayCacheProvider {
    private StorageServiceReplayCache cache;

    public InMemoryReplayCacheProvider() {
        try {
            final var storageService = new MemoryStorageService();
            storageService.setId("pac4j-replay-storage");
            storageService.initialize();

            cache = new StorageServiceReplayCache();
            cache.setStorage(storageService);
            cache.initialize();
        } catch (final ComponentInitializationException e) {
            throw new SAMLException(e);
        }
    }

    @Override
    public ReplayCache get() {
        return cache;
    }
}
