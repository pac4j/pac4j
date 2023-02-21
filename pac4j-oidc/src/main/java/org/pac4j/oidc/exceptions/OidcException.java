package org.pac4j.oidc.exceptions;

import org.pac4j.core.exception.TechnicalException;

/**
 * Root exception for OIDC client.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcException extends TechnicalException {

    public OidcException(String message) {
        super(message);
    }

    public OidcException(Throwable t) {
        super(t);
    }

    public OidcException(String message, Throwable t) {
        super(message, t);
    }
}
