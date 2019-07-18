package org.pac4j.core.exception.http;

/**
 * A "redirection" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public abstract class RedirectionAction extends HttpAction {

    private static final long serialVersionUID = -4985071484085124623L;

    protected RedirectionAction(final int code) {
        super(code);
    }
}
