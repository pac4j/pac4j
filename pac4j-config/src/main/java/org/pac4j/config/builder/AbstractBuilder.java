package org.pac4j.config.builder;

import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract builder of the configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractBuilder implements PropertiesConstants {

    /** Constant <code>MAX_NUM_CLIENTS=100</code> */
    protected static final int MAX_NUM_CLIENTS = 100;
    /** Constant <code>MAX_NUM_AUTHENTICATORS=10</code> */
    protected static final int MAX_NUM_AUTHENTICATORS = 10;
    /** Constant <code>MAX_NUM_CUSTOM_PROPERTIES=5</code> */
    protected static final int MAX_NUM_CUSTOM_PROPERTIES = 5;
    /** Constant <code>MAX_NUM_ENCODERS=10</code> */
    protected static final int MAX_NUM_ENCODERS = 10;

    protected final Map<String, String> properties;

    protected final Map<String, Authenticator> authenticators;

    /**
     * <p>Constructor for AbstractBuilder.</p>
     *
     * @param properties a {@link java.util.Map} object
     */
    protected AbstractBuilder(final Map<String, String> properties) {
        this.properties = properties;
        this.authenticators = new HashMap<>();
    }

    /**
     * <p>Constructor for AbstractBuilder.</p>
     *
     * @param properties a {@link java.util.Map} object
     * @param authenticators a {@link java.util.Map} object
     */
    protected AbstractBuilder(final Map<String, String> properties, final Map<String, Authenticator> authenticators) {
        this.properties = properties;
        this.authenticators = authenticators;
    }

    /**
     * <p>concat.</p>
     *
     * @param value a {@link java.lang.String} object
     * @param num a int
     * @return a {@link java.lang.String} object
     */
    protected String concat(final String value, int num) {
        return value.concat(num == 0 ? Pac4jConstants.EMPTY_STRING : "." + num);
    }

    /**
     * <p>getProperty.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    protected String getProperty(final String name) {
        return properties.get(name);
    }

    /**
     * <p>getProperty.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param num a int
     * @return a {@link java.lang.String} object
     */
    protected String getProperty(final String name, final int num) {
        return getProperty(concat(name, num));
    }

    /**
     * <p>containsProperty.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param num a int
     * @return a boolean
     */
    protected boolean containsProperty(final String name, final int num) {
        return properties.containsKey(concat(name, num));
    }

    /**
     * <p>getPropertyAsBoolean.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param num a int
     * @return a boolean
     */
    protected boolean getPropertyAsBoolean(final String name, final int num) {
        return Boolean.valueOf(getProperty(name, num));
    }

    /**
     * <p>getPropertyAsInteger.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param num a int
     * @return a int
     */
    protected int getPropertyAsInteger(final String name, final int num) {
        return Integer.parseInt(getProperty(name, num));
    }

    /**
     * <p>getPropertyAsLong.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param num a int
     * @return a long
     */
    protected long getPropertyAsLong(final String name, final int num) {
        return Long.parseLong(getProperty(name, num));
    }

    /**
     * <p>getAuthenticator.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link org.pac4j.core.credentials.authenticator.Authenticator} object
     */
    protected Authenticator getAuthenticator(final String name) {
        if (AUTHENTICATOR_TEST_TOKEN.equals(name)) {
            return new SimpleTestTokenAuthenticator();
        } else if (AUTHENTICATOR_TEST_USERNAME_PASSWORD.equals(name)) {
            return new SimpleTestUsernamePasswordAuthenticator();
        } else {
            return authenticators.get(name);
        }
    }
}
