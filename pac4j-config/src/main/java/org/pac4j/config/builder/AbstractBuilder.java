package org.pac4j.config.builder;

import java.util.Map;

/**
 * An abstract builder of the configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractBuilder {

    protected static final int MAX_NUM_CLIENTS = 10;
    protected static final int MAX_NUM_AUTHENTICATORS = 10;
    protected static final int MAX_NUM_CUSTOM_PROPERTIES = 5;
    protected static final int MAX_NUM_ENCODERS = 10;

    protected final Map<String, String> properties;

    protected AbstractBuilder(final Map<String, String> properties) {
        this.properties = properties;
    }

    protected String concat(final String value, int num) {
        return value.concat(num == 0 ? "" : "." + num);
    }

    protected String getProperty(final String name) {
        return properties.get(name);
    }

    protected String getProperty(final String name, final int num) {
        return getProperty(concat(name, num));
    }

    protected boolean containsProperty(final String name, final int num) {
        return properties.containsKey(concat(name, num));
    }

    protected boolean getPropertyAsBoolean(final String name, final int num) {
        return Boolean.valueOf(getProperty(name, num));
    }

    protected int getPropertyAsInteger(final String name, final int num) {
        return Integer.parseInt(getProperty(name, num));
    }

    protected long getPropertyAsLong(final String name, final int num) {
        return Long.parseLong(getProperty(name, num));
    }
}
