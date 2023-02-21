package org.pac4j.oidc.exceptions;

/**
 * Exception indicating an invalid OIDC client configuration.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcConfigurationException extends OidcException {

    public OidcConfigurationException(String message) {
        super(message);
    }

    public OidcConfigurationException(Throwable t) {
        super(t);
    }

    public OidcConfigurationException(String message, Throwable t) {
        super(message, t);
    }
}
