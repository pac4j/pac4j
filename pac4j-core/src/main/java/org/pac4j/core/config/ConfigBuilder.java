package org.pac4j.core.config;

import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To build a configuration from a factory.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ConfigBuilder {

    private final static Logger logger = LoggerFactory.getLogger(ConfigBuilder.class);

    @SuppressWarnings("unchecked")
    public synchronized static Config build(final String factoryName, final Object... parameters) {
        try {
            logger.info("Build the configuration from factory: {}", factoryName);

            final var factory = (ConfigFactory) org.pac4j.core.util.CommonHelper.getConstructor( factoryName ).newInstance();
            return factory.build(parameters);
        } catch (final Exception e) {
            throw new TechnicalException("Cannot build configuration", e);
        }
    }
}
