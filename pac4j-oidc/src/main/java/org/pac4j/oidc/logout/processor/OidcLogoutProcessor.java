package org.pac4j.oidc.logout.processor;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.SessionKeyCredentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.logout.processor.LogoutProcessor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.config.OidcConfiguration;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * The OIDC logout processor.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public class OidcLogoutProcessor implements LogoutProcessor {

    protected OidcConfiguration configuration;

    public OidcLogoutProcessor(final OidcConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    @Override
    public HttpAction processLogout(final CallContext ctx, final Credentials logoutCredentials) {
        assertTrue(logoutCredentials instanceof SessionKeyCredentials, "credentials must be of type SessionKeyCredentials");
        val credentials = (SessionKeyCredentials) logoutCredentials;
        val sessionKey = credentials.getSessionKey();

        val sessionLogoutHandler = configuration.findSessionLogoutHandler();
        if (credentials.getLogoutType() == LogoutType.BACK) {
            sessionLogoutHandler.destroySessionBack(ctx, sessionKey);
        } else {
            sessionLogoutHandler.destroySessionFront(ctx, sessionKey);
        }

        val webContext = ctx.webContext();
        webContext.setResponseHeader("Cache-Control", "no-cache, no-store");
        webContext.setResponseHeader("Pragma", "no-cache");
        return new OkAction(Pac4jConstants.EMPTY_STRING);
    }
}
