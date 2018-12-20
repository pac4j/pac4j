package org.pac4j.core.profile.factory;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

import java.util.function.Function;

/**
 * For classes that can set the profile manager factory.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ProfileManagerFactoryAware<C extends WebContext> {

    private static final Function<WebContext, ProfileManager> DEFAULT_PROFILE_MANAGER_FACTORY =
        webContext -> new ProfileManager(webContext);

    private Function<C, ProfileManager> profileManagerFactory;

    protected ProfileManager getProfileManager(final C context) {
        if (profileManagerFactory != null) {
            return profileManagerFactory.apply(context);
        } else if (Config.getProfileManagerFactory() != null) {
            return Config.getProfileManagerFactory().apply(context);
        } else {
            return DEFAULT_PROFILE_MANAGER_FACTORY.apply(context);
        }
    }

    public Function<C, ProfileManager> getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public void setProfileManagerFactory(final Function<C, ProfileManager> factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory = factory;
    }
}
