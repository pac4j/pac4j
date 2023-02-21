package org.pac4j.oidc.exceptions;

/**
 * Exception indicating that the OIDC state is missing from the session.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcMissingSessionStateException extends OidcException {

    public OidcMissingSessionStateException(String message) {
        super(message);
    }

    public OidcMissingSessionStateException(Throwable t) {
        super(t);
    }

    public OidcMissingSessionStateException(String message, Throwable t) {
        super(message, t);
    }
}
