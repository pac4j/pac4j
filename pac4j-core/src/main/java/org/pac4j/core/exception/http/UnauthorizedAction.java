package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * An unauthorized HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class UnauthorizedAction extends HttpAction {

    public static final UnauthorizedAction INSTANCE = new UnauthorizedAction();
    private static final long serialVersionUID = -7291712846651414978L;

    protected UnauthorizedAction() {
        super(HttpConstants.UNAUTHORIZED);
    }
}
