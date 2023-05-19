package org.pac4j.jee.http.adapter;

import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.jee.context.JEEContext;

import java.io.IOException;

/**
 * The HTTP action adapter for the {@link JEEContext}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class JEEHttpActionAdapter implements HttpActionAdapter {

    /** Constant <code>INSTANCE</code> */
    public static final HttpActionAdapter INSTANCE = new JEEHttpActionAdapter();

    /**
     * <p>Constructor for JEEHttpActionAdapter.</p>
     */
    protected JEEHttpActionAdapter() {}

    /** {@inheritDoc} */
    @Override
    public Object adapt(final HttpAction action, final WebContext context) {
        if (action != null) {
            var code = action.getCode();
            val response = ((JEEContext) context).getNativeResponse();

            if (code < 400) {
                response.setStatus(code);
            } else {
                try {
                    response.sendError(code);
                } catch (final IOException e) {
                    throw new TechnicalException(e);
                }
            }

            if (action instanceof WithLocationAction withLocationAction) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());

            } else if (action instanceof WithContentAction withContentAction) {
                val content = withContentAction.getContent();

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
