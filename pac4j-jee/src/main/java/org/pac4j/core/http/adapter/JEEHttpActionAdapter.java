package org.pac4j.core.http.adapter;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.*;

import java.io.IOException;

/**
 * Use the pac4j-javaee dependency instead.
 */
@Deprecated
public class JEEHttpActionAdapter implements HttpActionAdapter {

    public static final JEEHttpActionAdapter INSTANCE = new JEEHttpActionAdapter();

    protected JEEHttpActionAdapter() {}

    @Override
    public Object adapt(final HttpAction action, final WebContext context) {
        if (action != null) {
            var code = action.getCode();
            final var response = ((JEEContext) context).getNativeResponse();

            if (code < 400) {
                response.setStatus(code);
            } else {
                try {
                    response.sendError(code);
                } catch (final IOException e) {
                    throw new TechnicalException(e);
                }
            }

            if (action instanceof WithLocationAction) {
                final var withLocationAction = (WithLocationAction) action;
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());

            } else if (action instanceof WithContentAction) {
                final var withContentAction = (WithContentAction) action;
                final var content = withContentAction.getContent();

                if (content != null) {
                    try {
                        response.getWriter().write(content);
                    } catch (final IOException e) {
                        throw new TechnicalException(e);
                    }
                }
            }

            return null;
        }

        throw new TechnicalException("No action provided");
    }
}
