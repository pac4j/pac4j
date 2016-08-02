package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;

/**
 * This class handles logout but does not perform it.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class NoLogoutHandler<C extends WebContext> implements CasLogoutHandler<C> {
}
