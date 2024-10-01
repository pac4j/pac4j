package org.pac4j.cas.logout.processor;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.SessionKeyCredentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.NoContentAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.core.logout.processor.LogoutProcessor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * The CAS logout processor.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Slf4j
public class CasLogoutProcessor implements LogoutProcessor {

    protected CasConfiguration configuration;

    protected SessionLogoutHandler sessionLogoutHandler;

    /**
     * <p>Constructor for CasLogoutProcessor.</p>
     *
     * @param configuration a {@link CasConfiguration} object
     * @param sessionLogoutHandler a {@link SessionLogoutHandler} object
     */
    public CasLogoutProcessor(final CasConfiguration configuration, final SessionLogoutHandler sessionLogoutHandler) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.sessionLogoutHandler = sessionLogoutHandler;
    }

    /** {@inheritDoc} */
    @Override
    public HttpAction processLogout(final CallContext ctx, final Credentials logoutCredentials) {
        assertTrue(logoutCredentials instanceof SessionKeyCredentials, "credentials must be of type SessionKeyCredentials");
        val credentials = (SessionKeyCredentials) logoutCredentials;
        val sessionKey = credentials.getSessionKey();

        if (credentials.getLogoutType() == LogoutType.BACK) {
            if (StringUtils.isNotBlank(sessionKey) && sessionLogoutHandler != null) {
                sessionLogoutHandler.destroySession(ctx, sessionKey);
            }
            LOGGER.debug("back logout: no content returned");
            return NoContentAction.INSTANCE;
        } else {
            if (StringUtils.isNotBlank(sessionKey) && sessionLogoutHandler != null) {
                sessionLogoutHandler.destroySession(ctx, sessionKey);
            }
            val action = getFinalActionForFrontChannelLogout(ctx.webContext());
            LOGGER.debug("front logout, returning: {}", action);
            return action;
        }
    }

    /**
     * <p>getFinalActionForFrontChannelLogout.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link HttpAction} object
     */
    protected HttpAction getFinalActionForFrontChannelLogout(final WebContext context) {
        val relayStateValue = context.getRequestParameter(CasConfiguration.RELAY_STATE_PARAMETER);
        // if we have a state value -> redirect to the CAS server to continue the logout process
        if (relayStateValue.isPresent()) {
            val buffer = new StringBuilder();
            buffer.append(configuration.getPrefixUrl());
            if (!configuration.getPrefixUrl().endsWith("/")) {
                buffer.append("/");
            }
            buffer.append("logout?_eventId=next&");
            buffer.append(CasConfiguration.RELAY_STATE_PARAMETER);
            buffer.append("=");
            buffer.append(CommonHelper.urlEncode(relayStateValue.get()));
            val redirectUrl = buffer.toString();
            LOGGER.debug("Redirection url to the CAS server: {}", redirectUrl);
            return HttpActionHelper.buildRedirectUrlAction(context, redirectUrl);
        } else {
            return new OkAction(Pac4jConstants.EMPTY_STRING);
        }
    }
}
