package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.handler.LogoutHandler;

/**
 * Use the {@link LogoutHandler} instead.
 */
@Deprecated
public interface CasLogoutHandler<C extends WebContext> extends LogoutHandler<C> {
}
