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
            
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final Class<ConfigFactory> clazz;
            if (tccl == null) {
                clazz = (Class<ConfigFactory>) Class.forName(factoryName);
            } else {
                clazz = (Class<ConfigFactory>) Class.forName(factoryName, true, tccl);
            }
            final ConfigFactory factory = clazz.getDeclaredConstructor().newInstance();
            return factory.build(parameters);
        } catch (final Exception e) {
            throw new TechnicalException("Cannot build configuration", e);
        }
    }
}
