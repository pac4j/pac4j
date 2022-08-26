package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * A forbidden HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class ForbiddenAction extends HttpAction implements WithContentAction {

    @Deprecated
    public static final ForbiddenAction INSTANCE = new ForbiddenAction();
    private static final long serialVersionUID = 6661068865264199225L;

    private String content = Pac4jConstants.EMPTY_STRING;

    public ForbiddenAction() {
        super(HttpConstants.FORBIDDEN);
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
