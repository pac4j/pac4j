package org.pac4j.core.profile.factory;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;

import java.util.function.BiFunction;

/**
 * A {@link ProfileManager} factory based on the {@link WebContext} and on the {@link SessionStore}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public interface ProfileManagerFactory2 extends BiFunction<WebContext, SessionStore, ProfileManager> {

    ProfileManagerFactory2 DEFAULT = (context, store) -> new ProfileManager(context, store);
}
