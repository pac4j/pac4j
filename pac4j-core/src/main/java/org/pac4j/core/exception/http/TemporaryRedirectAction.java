package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * A temporary redirect action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class TemporaryRedirectAction extends RedirectionAction implements WithLocationAction {

    private static final long serialVersionUID = 1065181175886203423L;
    private final String location;

    public TemporaryRedirectAction(final String location) {
        super(HttpConstants.TEMPORARY_REDIRECT);
        this.location = location;
    }

    @Override
    public String getLocation() {
        return location;
    }
}
