package org.pac4j.cas.logout;

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.WebContext;

/**
 * This class handles logout but does not perform it.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class NoLogoutHandler implements LogoutHandler {
    
    private static final String LOGOUT_REQUEST_PARAMETER = "logoutRequest";
    
    @Override
    public boolean isTokenRequest(final WebContext context) {
        return context.getRequestParameter(CasClient.SERVICE_TICKET_PARAMETER) != null;
    }
    
    @Override
    public boolean isLogoutRequest(final WebContext context) {
        return "POST".equals(context.getRequestMethod())
               && context.getRequestParameter(LOGOUT_REQUEST_PARAMETER) != null;
    }
    
    @Override
    public void recordSession(final WebContext context, final String ticket) {
    }
    
    @Override
    public void destroySession(final WebContext context) {
    }
}
