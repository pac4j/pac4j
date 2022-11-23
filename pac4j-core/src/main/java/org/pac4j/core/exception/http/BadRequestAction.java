package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * A bad request action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class BadRequestAction extends HttpAction implements WithContentAction {

    private static final long serialVersionUID = 9190468211708168035L;

    private String content = Pac4jConstants.EMPTY_STRING;

    public BadRequestAction() {
        super(HttpConstants.BAD_REQUEST);
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
