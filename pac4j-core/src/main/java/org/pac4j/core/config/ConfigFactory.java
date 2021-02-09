package org.pac4j.core.config;

/**
 * A factory to build the configuration.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@FunctionalInterface
public interface ConfigFactory {

    /**
     * Build a configuration.
     *
     * @param parameters the parameters to build the configuration
     * @return the built configuration
     */
    Config build(Object... parameters);
}
