package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * A no content HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class NoContentAction extends HttpAction implements WithContentAction {

    public static final NoContentAction INSTANCE = new NoContentAction();

    protected NoContentAction() {
        super(HttpConstants.NO_CONTENT);
    }

    @Override
    public String getContent() {
        return "";
    }
}
