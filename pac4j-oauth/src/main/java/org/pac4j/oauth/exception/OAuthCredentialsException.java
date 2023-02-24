package org.pac4j.oauth.exception;

import org.pac4j.core.exception.CredentialsException;

import java.util.*;

/**
 * This class represents an exception occurring during OAuth credentials retrieval.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class OAuthCredentialsException extends CredentialsException {

    private static final long serialVersionUID = -3540979749535811079L;

    /** Constant <code>ERROR="error"</code> */
    public static final String ERROR = "error";

    /** Constant <code>ERROR_REASON="error_reason"</code> */
    public static final String ERROR_REASON = "error_reason";

    /** Constant <code>ERROR_DESCRIPTION="error_description"</code> */
    public static final String ERROR_DESCRIPTION = "error_description";

    private static final String ERROR_URI = "error_uri";

    /** Constant <code>ERROR_NAMES</code> */
    public static final List<String> ERROR_NAMES = Collections.unmodifiableList(Arrays.asList(
            new String[] {ERROR, ERROR_REASON, ERROR_DESCRIPTION, ERROR_URI}
    ));

    private final Map<String, String> errorMessages = new HashMap<>();

    /**
     * <p>Constructor for OAuthCredentialsException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public OAuthCredentialsException(final String message) {
        super(message);
    }

    /**
     * <p>setErrorMessage.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param message a {@link java.lang.String} object
     */
    public void setErrorMessage(final String name, final String message) {
        this.errorMessages.put(name, message);
    }

    /**
     * <p>Getter for the field <code>errorMessages</code>.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> getErrorMessages() {
        return this.errorMessages;
    }

    /**
     * <p>getError.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getError() {
        return this.errorMessages.get(ERROR);
    }

    /**
     * <p>getErrorReason.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getErrorReason() {
        return this.errorMessages.get(ERROR_REASON);
    }

    /**
     * <p>getErrorDescription.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getErrorDescription() {
        return this.errorMessages.get(ERROR_DESCRIPTION);
    }

    /**
     * <p>getErrorUri.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getErrorUri() {
        return this.errorMessages.get(ERROR_URI);
    }
}
