package org.pac4j.core.engine.savedrequest;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
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

    /** {@inheritDoc} */
    @Override
    public void save(final CallContext ctx) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        val requestedUrl = getRequestedUrl(webContext, sessionStore);
        if (WebContextHelper.isPost(webContext)) {
            LOGGER.debug("requestedUrl with data: {}", requestedUrl);
            val formPost = HttpActionHelper.buildFormPostContent(webContext);
            sessionStore.set(webContext, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        } else {
            LOGGER.debug("requestedUrl: {}", requestedUrl);
            sessionStore.set(webContext, Pac4jConstants.REQUESTED_URL, requestedUrl);
        }
    }

    /**
     * <p>getRequestedUrl.</p>
     *
     * @param context a {@link WebContext} object
     * @param sessionStore a {@link SessionStore} object
     * @return a {@link String} object
     */
    protected String getRequestedUrl(final WebContext context, final SessionStore sessionStore) {
        return context.getFullRequestURL();
    }

    /** {@inheritDoc} */
    @Override
    public HttpAction restore(final CallContext ctx, final String defaultUrl) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        val optRequestedUrl = sessionStore.get(webContext, Pac4jConstants.REQUESTED_URL);
        HttpAction requestedAction = null;
        if (optRequestedUrl.isPresent()) {
            sessionStore.set(webContext, Pac4jConstants.REQUESTED_URL, null);
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
            return HttpActionHelper.buildRedirectUrlAction(webContext, ((FoundAction) requestedAction).getLocation());
        } else {
            return HttpActionHelper.buildFormPostContentAction(webContext, ((OkAction) requestedAction).getContent());
        }
    }
}
