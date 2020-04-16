package org.pac4j.core.profile.factory;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.FindBest;

/**
 * For classes that can set the profile manager factory.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class ProfileManagerFactory2Aware<C extends WebContext> {

    private ProfileManagerFactory2 profileManagerFactory2;

    protected ProfileManager<UserProfile> getProfileManager(final C context, final SessionStore<C> sessionStore) {
        return FindBest.profileManagerFactory2(this.profileManagerFactory2, Config.INSTANCE, ProfileManagerFactory2.DEFAULT)
            .apply(context, sessionStore);
    }

    public ProfileManagerFactory2 getProfileManagerFactory2() {
        return profileManagerFactory2;
    }

    public void setProfileManagerFactory2(final ProfileManagerFactory2 factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory2 = factory;
    }
}
