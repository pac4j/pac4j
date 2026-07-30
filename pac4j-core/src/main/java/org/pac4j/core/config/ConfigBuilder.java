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

    private static final Object LOCK = new Object();

    /**
     * <p>build.</p>
     *
     * @param factoryName a {@link String} object
     * @param parameters a {@link Object} object
     * @return a {@link Config} object
     */
    @SuppressWarnings("unchecked")
    public static Config build(final String factoryName, final Object... parameters) {
        synchronized (LOCK) {
            try {
                LOGGER.info("Build the configuration from factory: {}", factoryName);

                val factory = (ConfigFactory) CommonHelper.getConstructor(factoryName).newInstance();
                return factory.build(parameters);
            } catch (final Exception e) {
                throw new TechnicalException("Cannot build configuration", e);
            }
        }
    }
}
