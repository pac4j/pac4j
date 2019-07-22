package org.pac4j.core.exception.http;

/**
 * An HTTP action with just a specific status.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class StatusAction extends HttpAction {

    private static final long serialVersionUID = -1512800910066851787L;

    public StatusAction(final int code) {
        super(code);
    }
}
