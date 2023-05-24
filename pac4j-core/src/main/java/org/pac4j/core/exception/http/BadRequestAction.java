package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.Pac4jConstants;

import java.io.Serial;

/**
 * A bad request action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BadRequestAction extends HttpAction implements WithContentAction {

    @Serial
    private static final long serialVersionUID = 9190468211708168035L;

    private String content = Pac4jConstants.EMPTY_STRING;

    /**
     * <p>Constructor for BadRequestAction.</p>
     */
    public BadRequestAction() {
        super(HttpConstants.BAD_REQUEST);
    }
}
