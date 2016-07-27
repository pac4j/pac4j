package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;

/**
 * This class handles logout but does not perform it.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class NoLogoutHandler<C extends WebContext> extends CasLogoutHandler<C> {

    @Override
    public void recordSession(final C context, final String ticket) {
    }

    @Override
    public void destroySession(final C context, final String sessionId) {
    }
}
