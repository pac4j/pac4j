package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * An unauthorized HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class UnauthorizedAction extends HttpAction implements WithContentAction {

    @Deprecated
    public static final UnauthorizedAction INSTANCE = new UnauthorizedAction();
    private static final long serialVersionUID = -7291712846651414978L;

    private String content = Pac4jConstants.EMPTY_STRING;

    public UnauthorizedAction() {
        super(HttpConstants.UNAUTHORIZED);
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "code", this.code, "content", content);
    }
}