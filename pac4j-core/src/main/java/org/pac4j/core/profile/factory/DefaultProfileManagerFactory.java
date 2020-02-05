package org.pac4j.core.profile.factory;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;

/**
 * The default {@link ProfileManagerFactory}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class DefaultProfileManagerFactory implements ProfileManagerFactory {

    public static final DefaultProfileManagerFactory INSTANCE = new DefaultProfileManagerFactory();

    @Override
    public ProfileManager apply(final WebContext context) {
        return new ProfileManager(context);
    }
}
