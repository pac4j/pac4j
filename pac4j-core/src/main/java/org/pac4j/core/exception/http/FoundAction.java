package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;

/**
 * A "Found" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class FoundAction extends RedirectionAction implements WithLocationAction {

    private static final long serialVersionUID = 5155686595276189592L;
    private final String location;

    public FoundAction(final String location) {
        super(HttpConstants.FOUND);
        this.location = location;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "code", this.code, "location", location);
    }
}
