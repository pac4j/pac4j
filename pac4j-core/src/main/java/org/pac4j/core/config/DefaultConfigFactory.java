package org.pac4j.core.config;

/**
 * A default configuration factory.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultConfigFactory implements ConfigFactory {

    protected Config config;

    /**
     * <p>Constructor for DefaultConfigFactory.</p>
     *
     * @param config a {@link org.pac4j.core.config.Config} object
     */
    public DefaultConfigFactory(final Config config) {
        this.config = config;
    }

    /** {@inheritDoc} */
    @Override
    public Config build(final Object... parameters) {
        return config;
    }
}
