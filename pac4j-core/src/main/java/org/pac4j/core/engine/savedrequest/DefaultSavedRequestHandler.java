package org.pac4j.core.engine.savedrequest;

import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.util.HttpActionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default {@link SavedRequestHandler} which handles GET and POST requests.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class DefaultSavedRequestHandler implements SavedRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSavedRequestHandler.class);

    @Override
    public void save(final WebContext context, final SessionStore sessionStore) {
        final var requestedUrl = getRequestedUrl(context, sessionStore);
        if (WebContextHelper.isPost(context)) {
            LOGGER.debug("requestedUrl with data: {}", requestedUrl);
            final var formPost = HttpActionHelper.buildFormPostContent(context);
            sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        } else {
            LOGGER.debug("requestedUrl: {}", requestedUrl);
            sessionStore.set(context, Pac4jConstants.REQUESTED_URL, requestedUrl);
        }
    }

    protected String getRequestedUrl(final WebContext context, final SessionStore sessionStore) {
        return context.getFullRequestURL();
    }

    @Override
    public HttpAction restore(final WebContext context, final SessionStore sessionStore, final String defaultUrl) {
        final var optRequestedUrl = sessionStore.get(context, Pac4jConstants.REQUESTED_URL);
        HttpAction requestedAction = null;
        if (optRequestedUrl.isPresent()) {
            sessionStore.set(context, Pac4jConstants.REQUESTED_URL, null);
            final var requestedUrl = optRequestedUrl.get();
            if (requestedUrl instanceof String) {
                requestedAction = new FoundAction((String) requestedUrl);
            } else if (requestedUrl instanceof RedirectionAction) {
                requestedAction = (RedirectionAction) requestedUrl;
            }
        }
        if (requestedAction == null) {
            requestedAction = new FoundAction(defaultUrl);
        }

        LOGGER.debug("requestedAction: {}", requestedAction.getMessage());
        if (requestedAction instanceof FoundAction) {
            return HttpActionHelper.buildRedirectUrlAction(context, ((FoundAction) requestedAction).getLocation());
        } else {
            return HttpActionHelper.buildFormPostContentAction(context, ((OkAction) requestedAction).getContent());
        }
    }
}
