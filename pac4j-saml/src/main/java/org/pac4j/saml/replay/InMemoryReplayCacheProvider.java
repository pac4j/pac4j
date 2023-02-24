package org.pac4j.saml.replay;

import lombok.val;
import net.shibboleth.shared.component.ComponentInitializationException;
import org.opensaml.storage.ReplayCache;
import org.opensaml.storage.impl.MemoryStorageService;
import org.opensaml.storage.impl.StorageServiceReplayCache;
import org.pac4j.saml.exceptions.SAMLException;


/**
 * Default replay cache provider which stores the identifiers in memory. This
 * implementation will not work in a clustered environment and requires the same
 * instance is used for all SAML authentications.
 *
 * @author bidou
 */
public class InMemoryReplayCacheProvider implements ReplayCacheProvider {
    private StorageServiceReplayCache cache;

    /**
     * <p>Constructor for InMemoryReplayCacheProvider.</p>
     */
    public InMemoryReplayCacheProvider() {
        try {
            val storageService = new MemoryStorageService();
            storageService.setId("pac4j-replay-storage");
            storageService.initialize();

            cache = new StorageServiceReplayCache();
            cache.setStorage(storageService);
            cache.initialize();
        } catch (final ComponentInitializationException e) {
            throw new SAMLException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ReplayCache get() {
        return cache;
    }
}
