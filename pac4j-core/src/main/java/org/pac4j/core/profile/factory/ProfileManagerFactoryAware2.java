package org.pac4j.core.profile.factory;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

import java.util.function.BiFunction;

/**
 * For classes that can set the profile manager factory.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class ProfileManagerFactoryAware2<C extends WebContext> {

    private static final BiFunction<WebContext, SessionStore<WebContext>, ProfileManager> DEFAULT_PROFILE_MANAGER_FACTORY2 =
        (webContext, sessionStore)-> new ProfileManager(webContext, sessionStore);

    private BiFunction<C, SessionStore<C>, ProfileManager> profileManagerFactory2;

    protected ProfileManager getProfileManager(final C context, final SessionStore<C> sessionStore) {
        if (profileManagerFactory2 != null) {
            return profileManagerFactory2.apply(context, sessionStore);
        } else if (Config.getProfileManagerFactory2() != null) {
            return Config.getProfileManagerFactory2().apply(context, sessionStore);
        } else {
            return DEFAULT_PROFILE_MANAGER_FACTORY2.apply(context, (SessionStore<WebContext>) sessionStore);
        }
    }

    public BiFunction<C, SessionStore<C>, ProfileManager> getProfileManagerFactory2() {
        return profileManagerFactory2;
    }

    public void setProfileManagerFactory2(final BiFunction<C, SessionStore<C>, ProfileManager> factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory2 = factory;
    }
}
