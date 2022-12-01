package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.context.HttpConstants;

/**
 * A "Found" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@ToString(callSuper = true)
public class FoundAction extends RedirectionAction implements WithLocationAction {

    private static final long serialVersionUID = 5155686595276189592L;
    private final String location;

    public FoundAction(final String location) {
        super(HttpConstants.FOUND);
        this.location = location;
    }
}
