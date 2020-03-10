package org.pac4j.saml.replay;

import org.opensaml.storage.ReplayCache;
import org.opensaml.storage.impl.MemoryStorageService;
import org.pac4j.saml.exceptions.SAMLException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * Default replay cache provider which stores the identifiers in memory. This
 * implementation will not work in a clustered environment and requires the same
 * instance is used for all SAML authentications.
 */
public class InMemoryReplayCacheProvider implements ReplayCacheProvider {
    private ReplayCache cache;

    public InMemoryReplayCacheProvider() {
        try {
            final MemoryStorageService storageService = new MemoryStorageService();
            storageService.setId("pac4j-replay-storage");
            storageService.initialize();

            cache = new ReplayCache();
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
