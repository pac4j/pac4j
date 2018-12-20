package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * A temporary redirect HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class TemporaryRedirectAction extends RedirectionAction {

    private final String location;

    public TemporaryRedirectAction(final String location) {
        super(HttpConstants.TEMP_REDIRECT);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
