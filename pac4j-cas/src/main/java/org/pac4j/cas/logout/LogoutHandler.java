package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;

/**
 * Replaced by the {@link CasLogoutHandler}.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 * @deprecated
 */
@Deprecated
public interface LogoutHandler<C extends WebContext> {
    
    /**
     * Defines if this request is a token one.
     * 
     * @param context the web context
     * @return if this request is a token one
     */
    default boolean isTokenRequest(C context) { return false; }
    
    /**
     * Defines if this request is a logout one.
     * 
     * @param context the web context
     * @return if this request is a logout one
     */
    default boolean isLogoutRequest(C context) { return false; }
    
    /**
     * Associates a token request with the current web session.
     * 
     * @param context the web context
     * @param ticket the service ticket
     */
    void recordSession(C context, String ticket);
    
    /**
     * Destroys the current web session for the given CAS logout request.
     * 
     * @param context the web context
     */
    void destroySession(C context);
}
