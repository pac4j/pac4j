package org.pac4j.core.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

/**
 * To build a configuration from a factory.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Slf4j
public final class ConfigBuilder {

    /**
     * <p>build.</p>
     *
     * @param factoryName a {@link java.lang.String} object
     * @param parameters a {@link java.lang.Object} object
     * @return a {@link org.pac4j.core.config.Config} object
     */
    @SuppressWarnings("unchecked")
    public synchronized static Config build(final String factoryName, final Object... parameters) {
        try {
            LOGGER.info("Build the configuration from factory: {}", factoryName);

            val factory = (ConfigFactory) CommonHelper.getConstructor(factoryName).newInstance();
            return factory.build(parameters);
        } catch (final Exception e) {
            throw new TechnicalException("Cannot build configuration", e);
        }
    }
}
