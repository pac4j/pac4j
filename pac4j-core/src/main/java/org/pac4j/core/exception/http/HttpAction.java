package org.pac4j.core.exception.http;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

/**
 * The HTTP action, to perform on the web context by the appropriate {@link org.pac4j.core.http.adapter.HttpActionAdapter}.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class HttpAction extends TechnicalException {

    private static final long serialVersionUID = -3959659239684160075L;

    protected int code;

    protected HttpAction(final int code) {
        super("Performing a " + code + " HTTP action");
        this.code = code;
    }

    /**
     * Return the HTTP code.
     *
     * @return the HTTP code
     */
    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(HttpAction.class, "code", this.code);
    }
}
