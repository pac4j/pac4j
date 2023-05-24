package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.util.Pac4jConstants;

import java.io.Serial;

/**
 * An HTTP action with just a specific status and maybe a content.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class StatusAction extends HttpAction implements WithContentAction {

    @Serial
    private static final long serialVersionUID = -1512800910066851787L;

    private String content = Pac4jConstants.EMPTY_STRING;

    /**
     * <p>Constructor for StatusAction.</p>
     *
     * @param code a int
     */
    public StatusAction(final int code) {
        super(code);
    }
}
