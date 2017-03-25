package org.pac4j.core.config;

/**
 * A default configuration factory.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultConfigFactory implements ConfigFactory {

    protected Config config;

    public DefaultConfigFactory(final Config config) {
        this.config = config;
    }

    @Override
    public Config build(final Object... parameters) {
        return config;
    }
}
