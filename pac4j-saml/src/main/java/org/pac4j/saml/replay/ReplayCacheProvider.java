package org.pac4j.saml.replay;

import org.opensaml.storage.ReplayCache;

/**
 * Builds or resolves the replay cache that is used to prevent replay attacks.
 * It is important that this returns the same {@code ReplayCache} instance over
 * multiple invocations.
 *
 * @since 3.8
 * @author bidou
 */
@FunctionalInterface
public interface ReplayCacheProvider {
    /**
     * <p>get.</p>
     *
     * @return a {@link ReplayCache} object
     */
    ReplayCache get();
}
