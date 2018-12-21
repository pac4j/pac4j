package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * A "Found" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class FoundAction extends RedirectionAction {

    private final String location;

    public FoundAction(final String location) {
        super(HttpConstants.FOUND);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
