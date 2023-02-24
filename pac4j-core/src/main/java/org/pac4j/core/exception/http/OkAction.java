package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.context.HttpConstants;

/**
 * An OK HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@ToString(callSuper = true)
public class OkAction extends RedirectionAction implements WithContentAction {

    private static final long serialVersionUID = -8842651379112280831L;
    private final String content;

    /**
     * <p>Constructor for OkAction.</p>
     *
     * @param content a {@link java.lang.String} object
     */
    public OkAction(final String content) {
        super(HttpConstants.OK);
        this.content = content;
    }
}
