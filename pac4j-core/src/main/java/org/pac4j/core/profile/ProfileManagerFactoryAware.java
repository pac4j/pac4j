package org.pac4j.core.profile;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * For classes that can set the profile manager factory.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ProfileManagerFactoryAware<C extends WebContext> {

    private final Function<C, ProfileManager> DEFAULT_PROFILE_MANAGER_FACTORY = webContext -> new ProfileManager(webContext);

    private final BiFunction<C, SessionStore<C>, ProfileManager> DEFAULT_PROFILE_MANAGER_FACTORY2 =
        (webContext, sessionStore)-> new ProfileManager(webContext, sessionStore);

    private Function<C, ProfileManager> profileManagerFactory;

    private BiFunction<C, SessionStore<C>, ProfileManager> profileManagerFactory2;

    @Deprecated
    protected ProfileManager getProfileManager(final C context, final Config config) {
        return getProfileManager(context);
    }

    protected ProfileManager getProfileManager(final C context) {
        if (profileManagerFactory != null) {
            return profileManagerFactory.apply(context);
        } else if (Config.getProfileManagerFactory() != null) {
            return Config.getProfileManagerFactory().apply(context);
        } else {
            return DEFAULT_PROFILE_MANAGER_FACTORY.apply(context);
        }
    }

    protected ProfileManager getProfileManager(final C context, final SessionStore<C> sessionStore) {
        if (profileManagerFactory2 != null) {
            return profileManagerFactory2.apply(context, sessionStore);
        } else if (Config.getProfileManagerFactory2() != null) {
            return Config.getProfileManagerFactory2().apply(context, sessionStore);
        } else {
            return DEFAULT_PROFILE_MANAGER_FACTORY2.apply(context, sessionStore);
        }
    }

    public Function<C, ProfileManager> getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public void setProfileManagerFactory(final Function<C, ProfileManager> factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory = factory;
    }

    public BiFunction<C, SessionStore<C>, ProfileManager> getProfileManagerFactory2() {
        return profileManagerFactory2;
    }

    public void setProfileManagerFactory2(final BiFunction<C, SessionStore<C>, ProfileManager> factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory2 = factory;
    }
}
