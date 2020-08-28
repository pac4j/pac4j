package org.pac4j.core.client;

import org.pac4j.core.client.config.BaseClientConfiguration;

/**
 * Allowing clients to be aware of their configuration and
 * report it back to callers. Any client that wishes
 * to expose a configuration object should implement this interface.
 *
 * @author Misagh Moayyed
 * @since 4.0.4
 */
@FunctionalInterface
public interface ConfigurableClient<C extends BaseClientConfiguration> {
    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    C getConfiguration();
}
