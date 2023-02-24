package org.pac4j.oidc.exceptions;

/**
 * Exception indicating an invalid OIDC client configuration.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcConfigurationException extends OidcException {

    /**
     * <p>Constructor for OidcConfigurationException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public OidcConfigurationException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcConfigurationException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public OidcConfigurationException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcConfigurationException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public OidcConfigurationException(String message, Throwable t) {
        super(message, t);
    }
}
