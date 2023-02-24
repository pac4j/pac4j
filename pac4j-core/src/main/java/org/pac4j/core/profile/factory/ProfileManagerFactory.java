package org.pac4j.core.profile.factory;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;

import java.util.function.BiFunction;

/**
 * A {@link org.pac4j.core.profile.ProfileManager} factory based on the {@link org.pac4j.core.context.WebContext}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
@FunctionalInterface
public interface ProfileManagerFactory extends BiFunction<WebContext, SessionStore, ProfileManager> {

    /** Constant <code>DEFAULT</code> */
    ProfileManagerFactory DEFAULT = ProfileManager::new;
}
