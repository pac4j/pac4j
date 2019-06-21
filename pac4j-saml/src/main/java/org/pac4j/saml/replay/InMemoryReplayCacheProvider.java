package org.pac4j.saml.replay;

import org.opensaml.storage.ReplayCache;
import org.opensaml.storage.impl.MemoryStorageService;
import org.pac4j.saml.exceptions.SAMLException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class InMemoryReplayCacheProvider implements ReplayCacheProvider {
    private ReplayCache cache;

    public InMemoryReplayCacheProvider() {
        try {
            MemoryStorageService storageService = new MemoryStorageService();
            storageService.setId("pac4j-replay-storage");
            storageService.initialize();

            cache = new ReplayCache();
            cache.setStorage(storageService);
            cache.initialize();
        } catch (ComponentInitializationException e) {
            throw new SAMLException(e);
        }
    }

    @Override
    public ReplayCache get() {
        return cache;
    }
}
