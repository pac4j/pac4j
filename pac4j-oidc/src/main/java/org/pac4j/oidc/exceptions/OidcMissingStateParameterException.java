package org.pac4j.oidc.exceptions;

/**
 * Exception indicating that the OIDC state parameter is missing from the callback.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcMissingStateParameterException extends OidcException {

    public OidcMissingStateParameterException(String message) {
        super(message);
    }

    public OidcMissingStateParameterException(Throwable t) {
        super(t);
    }

    public OidcMissingStateParameterException(String message, Throwable t) {
        super(message, t);
    }
}
