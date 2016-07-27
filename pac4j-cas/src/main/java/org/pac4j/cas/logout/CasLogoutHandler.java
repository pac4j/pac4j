package org.pac4j.cas.logout;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interface defines how to handle CAS logout request on client side.
 * 
 * @author Jerome Leleu
 * @since 1.9.2
 */
public abstract class CasLogoutHandler<C extends WebContext> implements LogoutHandler<C> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void destroySession(C context) {
        final String logoutMessage = context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER);
        logger.trace("Logout request:\n{}", logoutMessage);

        final String sessionId = XmlUtils.getTextForElement(logoutMessage, CasConfiguration.SESSION_INDEX_TAG);
        if (CommonUtils.isNotBlank(sessionId)) {
            destroySession(context, sessionId);
        }
    }

    /**
     * Destroys the current web session for the given session identifier.
     * 
     * @param context the web context
     * @param sessionId the session identifier
     */
    public abstract void destroySession(C context, String sessionId);
}
