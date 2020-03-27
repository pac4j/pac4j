
package org.pac4j.saml.exceptions;

import org.pac4j.core.exception.TechnicalException;

/**
 * Root exception for SAML Client.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAMLException extends TechnicalException {

    private static final long serialVersionUID = -2963580056603469743L;

    public SAMLException(final String message) {
        super(message);
    }

    public SAMLException(final Throwable t) {
        super(t);
    }

    public SAMLException(final String message, final Throwable t) {
        super(message, t);
    }
}
