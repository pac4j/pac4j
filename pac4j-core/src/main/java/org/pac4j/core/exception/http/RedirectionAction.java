package org.pac4j.core.exception.http;

import java.io.Serial;

/**
 * A "redirection" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public abstract class RedirectionAction extends HttpAction {

    @Serial
    private static final long serialVersionUID = -4985071484085124623L;

    /**
     * <p>Constructor for RedirectionAction.</p>
     *
     * @param code a int
     */
    protected RedirectionAction(final int code) {
        super(code);
    }
}
