package org.pac4j.core.exception.http;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * An HTTP action with just a specific status and maybe a content.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class StatusAction extends HttpAction implements WithContentAction {

    private static final long serialVersionUID = -1512800910066851787L;

    private String content = Pac4jConstants.EMPTY_STRING;

    public StatusAction(final int code) {
        super(code);
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
