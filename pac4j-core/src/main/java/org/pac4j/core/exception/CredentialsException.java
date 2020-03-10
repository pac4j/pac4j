package org.pac4j.core.exception;

/**
 * This class represents an expected exception occurring during credentials retrieval.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CredentialsException extends TechnicalException {

    private static final long serialVersionUID = 6013115966613706463L;

    public CredentialsException(final String message) {
        super(message);
    }

    public CredentialsException(final Throwable t) {
        super(t);
    }

    public CredentialsException(final String message, final Throwable t) {
        super(message, t);
    }
}
