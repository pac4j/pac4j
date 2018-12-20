package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * A forbidden HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class ForbiddenAction extends HttpAction {

    public static final ForbiddenAction INSTANCE = new ForbiddenAction();

    protected ForbiddenAction() {
        super(HttpConstants.FORBIDDEN);
    }
}
