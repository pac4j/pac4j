package org.pac4j.core.engine.savedrequest;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The default {@link SavedRequestHandler} which handles GET and POST requests.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class DefaultSavedRequestHandler implements SavedRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSavedRequestHandler.class);

    @Override
    public void save(final WebContext context) {
        final String requestedUrl = context.getFullRequestURL();
        if (ContextHelper.isPost(context)) {
            LOGGER.debug("requestedUrl with data: {}", requestedUrl);
            final Map<String, String[]> parameters = new HashMap<>();
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL,
                OkAction.buildFormContentFromUrlAndData(requestedUrl, parameters));
        } else {
            LOGGER.debug("requestedUrl: {}", requestedUrl);
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(requestedUrl));
        }
    }

    @Override
    public HttpAction restore(final WebContext context, final String defaultUrl) {
        final Optional<Object> optRequestedUrl = context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL);
        HttpAction requestedAction = null;
        if (optRequestedUrl.isPresent()) {
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, "");
            final Object requestedUrl = optRequestedUrl.get();
            // handles String for backward compatibility
            if (requestedUrl instanceof String && !"".equals(requestedUrl)) {
                requestedAction = new FoundAction((String) requestedUrl);
            } else if (requestedUrl instanceof FoundAction) {
                requestedAction = (FoundAction) requestedUrl;
            } else if (requestedUrl instanceof OkAction) {
                requestedAction = (OkAction) requestedUrl;
            }
        }
        if (requestedAction == null) {
            requestedAction = new FoundAction(defaultUrl);
        }

        LOGGER.debug("requestedAction: {}", requestedAction);
        if (ContextHelper.isPost(context)) {
            if (requestedAction instanceof FoundAction) {
                return new SeeOtherAction(((FoundAction) requestedAction).getLocation());
            } else {
                return new TemporaryRedirectAction(((OkAction) requestedAction).getContent());
            }
        } else {
            return requestedAction;
        }
    }
}
