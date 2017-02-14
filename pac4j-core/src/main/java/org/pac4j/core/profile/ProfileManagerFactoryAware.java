package org.pac4j.core.profile;

import org.pac4j.core.config.Config;
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

    private final Function<C, ProfileManager> DEFAULT_PROFILE_MANAGER_FACTORY = ctx -> new ProfileManager(ctx);

    private Function<C, ProfileManager> profileManagerFactory;

    /**
     * Given a webcontext generate a profileManager for it.
     * Can be overridden for custom profile manager implementations
     * @param context the web context
     * @param config the configuration
     * @return profile manager implementation built from the context
     */
    protected ProfileManager getProfileManager(final C context, final Config config) {
        final Function<C, ProfileManager> configProfileManagerFactory = (Function<C, ProfileManager>) config.getProfileManagerFactory();
        if (configProfileManagerFactory != null) {
            return configProfileManagerFactory.apply(context);
        } else if (profileManagerFactory != null) {
            return profileManagerFactory.apply(context);
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
