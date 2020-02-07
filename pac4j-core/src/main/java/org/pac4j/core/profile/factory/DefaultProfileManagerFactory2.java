package org.pac4j.core.profile.factory;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;

/**
 * The default {@link ProfileManagerFactory2}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class DefaultProfileManagerFactory2 implements ProfileManagerFactory2 {

    public static final DefaultProfileManagerFactory2 INSTANCE = new DefaultProfileManagerFactory2();

    @Override
    public ProfileManager apply(final WebContext context, final SessionStore store) {
        return new ProfileManager(context, store);
    }
}
