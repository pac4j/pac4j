package org.pac4j.core.profile;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.function.Function;

/**
 * For classes that can set the profile manager factory.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ProfileManagerFactoryAware<C extends WebContext> {

    private Function<C, ProfileManager> profileManagerFactory = context -> new ProfileManager(context);

    /**
     * Given a webcontext generate a profileManager for it.
     * Can be overridden for custom profile manager implementations
     * @param context the web context
     * @return profile manager implementation built from the context
     */
    protected ProfileManager getProfileManager(final C context) {
        return profileManagerFactory.apply(context);
    }

    public Function<C, ProfileManager> getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public void setProfileManagerFactory(final Function<C, ProfileManager> factory) {
        CommonHelper.assertNotNull("factory", factory);
        this.profileManagerFactory = factory;
    }
}
