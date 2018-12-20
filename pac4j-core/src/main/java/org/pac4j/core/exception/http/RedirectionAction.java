package org.pac4j.core.exception.http;

/**
 * A "redirection" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public abstract class RedirectionAction extends HttpAction {

    protected RedirectionAction(final int code) {
        super(code);
    }
}
