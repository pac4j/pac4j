package org.pac4j.oidc.exceptions;

/**
 * Exception indicating a mismatch between the metadata issuer and the response issuer.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcIssuerMismatchException extends OidcException {

    public OidcIssuerMismatchException(String message) {
        super(message);
    }

    public OidcIssuerMismatchException(Throwable t) {
        super(t);
    }

    public OidcIssuerMismatchException(String message, Throwable t) {
        super(message, t);
    }
}
