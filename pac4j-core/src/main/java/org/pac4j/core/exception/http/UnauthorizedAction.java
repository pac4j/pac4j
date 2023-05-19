package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.Pac4jConstants;

import java.io.Serial;

/**
 * An unauthorized HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UnauthorizedAction extends HttpAction implements WithContentAction {

    @Serial
    private static final long serialVersionUID = -7291712846651414978L;

    private String content = Pac4jConstants.EMPTY_STRING;

    /**
     * <p>Constructor for UnauthorizedAction.</p>
     */
    public UnauthorizedAction() {
        super(HttpConstants.UNAUTHORIZED);
    }
}
