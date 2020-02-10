package org.pac4j.core.profile.factory;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.FindBest;

/**
 * For classes that can set the profile manager factory.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ProfileManagerFactoryAware<C extends WebContext> {

    private ProfileManagerFactory profileManagerFactory;

    protected ProfileManager getProfileManager(final C context) {
        return FindBest.profileManagerFactory(this.profileManagerFactory, Config.INSTANCE, ProfileManagerFactory.DEFAULT)
            .apply(context);
    }

    public ProfileManagerFactory getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public void setProfileManagerFactory(final ProfileManagerFactory factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory = factory;
    }
}
