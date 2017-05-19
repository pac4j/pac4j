package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;

/**
* This method provides a way for a direct client to request additional
* information from browser for direct clients that support it
* 
* @param context the web context
* @since 1.9.1
*/
public interface DirectExtended {
    void handleForbidden(final WebContext context);
}
