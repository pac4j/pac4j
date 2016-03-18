package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;

/**
 * This interface defines how to handle CAS logout request on client side.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface LogoutHandler {
    
    /**
     * Defines if this request is a token one.
     * 
     * @param context the web context
     * @return if this request is a token one
     */
    boolean isTokenRequest(WebContext context);
    
    /**
     * Defines if this request is a logout one.
     * 
     * @param context the web context
     * @return if this request is a logout one
     */
    boolean isLogoutRequest(WebContext context);
    
    /**
     * Associates a token request with the current web session.
     * 
     * @param context the web context
     * @param ticket the service ticket
     */
    void recordSession(WebContext context, String ticket);
    
    /**
     * Destroys the current web session for the given CAS logout request.
     * 
     * @param context the web context
     */
    void destroySession(WebContext context);
}
