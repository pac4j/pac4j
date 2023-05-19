package org.pac4j.core.exception;

import java.io.Serial;

/**
 * This class represents an expected exception occurring during credentials retrieval.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CredentialsException extends TechnicalException {

    @Serial
    private static final long serialVersionUID = 6013115966613706463L;

    /**
     * <p>Constructor for CredentialsException.</p>
     *
     * @param message a {@link String} object
     */
    public CredentialsException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for CredentialsException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public CredentialsException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for CredentialsException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public CredentialsException(String message, Throwable t) {
        super(message, t);
    }
}
