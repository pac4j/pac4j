package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.store.Store;

/**
 * Use the {@link DefaultLogoutHandler} instead.
 */
@Deprecated
public class DefaultCasLogoutHandler<C extends WebContext> extends DefaultLogoutHandler<C> implements CasLogoutHandler<C> {

    public DefaultCasLogoutHandler() {}

    public DefaultCasLogoutHandler(final Store<String, Object> store) {
        super(store);
    }
}
