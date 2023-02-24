package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.exception.TechnicalException;

/**
 * The HTTP action, to perform on the web context by the appropriate {@link org.pac4j.core.http.adapter.HttpActionAdapter}.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@Getter
@ToString
public abstract class HttpAction extends TechnicalException {

    private static final long serialVersionUID = -3959659239684160075L;

    protected int code;

    /**
     * <p>Constructor for HttpAction.</p>
     *
     * @param code a int
     */
    protected HttpAction(final int code) {
        super("Performing a " + code + " HTTP action");
        this.code = code;
    }
}
