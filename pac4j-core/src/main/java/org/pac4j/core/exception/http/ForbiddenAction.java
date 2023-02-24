package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.Pac4jConstants;

/**
 * A forbidden HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ForbiddenAction extends HttpAction implements WithContentAction {

    private static final long serialVersionUID = 6661068865264199225L;

    private String content = Pac4jConstants.EMPTY_STRING;

    /**
     * <p>Constructor for ForbiddenAction.</p>
     */
    public ForbiddenAction() {
        super(HttpConstants.FORBIDDEN);
    }
}
