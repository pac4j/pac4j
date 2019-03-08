package org.pac4j.core.exception.http;

import org.pac4j.core.context.HttpConstants;

/**
 * A temporary redirect action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class TemporaryRedirectAction extends RedirectionAction implements WithContentAction {

    private final String content;

    public TemporaryRedirectAction(final String content) {
        super(HttpConstants.TEMPORARY_REDIRECT);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
