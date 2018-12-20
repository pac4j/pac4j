package org.pac4j.core.exception.http;

/**
 * An HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class StatusAction extends HttpAction {

    public StatusAction(final int code) {
        super(code);
    }
}
