package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;

/**
 * An OK HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class OkAction extends RedirectionAction implements WithContentAction {

    private static final long serialVersionUID = -8842651379112280831L;
    private final String content;

    public OkAction(final String content) {
        super(HttpConstants.OK);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "code", this.code, "content", content);
    }
}
