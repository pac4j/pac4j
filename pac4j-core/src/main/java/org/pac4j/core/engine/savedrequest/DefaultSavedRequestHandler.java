package org.pac4j.core.engine.savedrequest;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * The default {@link SavedRequestHandler} which handles GET and POST requests.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
@Slf4j
public class DefaultSavedRequestHandler implements SavedRequestHandler {

    @Override
    public void save(final WebContext context, final SessionStore sessionStore) {
        val requestedUrl = getRequestedUrl(context, sessionStore);
        if (WebContextHelper.isPost(context)) {
            LOGGER.debug("requestedUrl with data: {}", requestedUrl);
            val formPost = HttpActionHelper.buildFormPostContent(context);
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
        val optRequestedUrl = sessionStore.get(context, Pac4jConstants.REQUESTED_URL);
        HttpAction requestedAction = null;
        if (optRequestedUrl.isPresent()) {
            sessionStore.set(context, Pac4jConstants.REQUESTED_URL, null);
            val requestedUrl = optRequestedUrl.get();
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
