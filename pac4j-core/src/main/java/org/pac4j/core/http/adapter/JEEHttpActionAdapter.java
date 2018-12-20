package org.pac4j.core.http.adapter;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.*;

/**
 * The HTTP action adapter for the {@link JEEContext}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class JEEHttpActionAdapter implements HttpActionAdapter<Object, JEEContext> {

    public static final JEEHttpActionAdapter INSTANCE = new JEEHttpActionAdapter();

    @Override
    public Object adapt(final HttpAction action, final JEEContext context) {
        if (action != null) {
            context.setResponseStatus(action.getCode());

            if (action instanceof NoContentAction) {
                context.writeResponseContent("");
            } else if (action instanceof OkAction) {
                context.writeResponseContent(((OkAction) action).getContent());
            } else if (action instanceof TemporaryRedirectAction) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, ((TemporaryRedirectAction) action).getLocation());
            } else if (action instanceof SeeOtherAction) {
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, ((SeeOtherAction) action).getLocation());
            }

            return null;
        }

        throw new TechnicalException("No action provided");
    }
}
