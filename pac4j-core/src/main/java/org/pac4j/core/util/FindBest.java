package org.pac4j.core.util;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

/**
 * Utility class to find the best adapter, logic... in the following order:
 * 1) the local one
 * 2) the one from the config
 * 3) the default one (must not be null).
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class FindBest {

    public static HttpActionAdapter httpActionAdapter(final HttpActionAdapter localAdapter, final Config config,
                                                      final HttpActionAdapter defaultAdapter) {
        if (localAdapter != null) {
            return localAdapter;
        } else if (config != null && config.getHttpActionAdapter() != null) {
            return config.getHttpActionAdapter();
        } else {
            CommonHelper.assertNotNull("defaultAdapter", defaultAdapter);
            return defaultAdapter;
        }
    }

    public static SessionStore sessionStore(final SessionStore localSessionStore, final Config config,
                                            final SessionStore defaultSessionStore) {
        if (localSessionStore != null) {
            return localSessionStore;
        } else if (config != null && config.getSessionStore() != null) {
            return config.getSessionStore();
        } else {
            CommonHelper.assertNotNull("defaultSessionStore", defaultSessionStore);
            return defaultSessionStore;
        }
    }

    public static ProfileManagerFactory profileManagerFactory(final ProfileManagerFactory localProfileManagerFactory, final Config config,
                                                              final ProfileManagerFactory defaultProfileManagerFactory) {
        if (localProfileManagerFactory != null) {
            return localProfileManagerFactory;
        } else if (config != null && config.getProfileManagerFactory() != null) {
            return config.getProfileManagerFactory();
        } else {
            CommonHelper.assertNotNull("defaultProfileManagerFactory", defaultProfileManagerFactory);
            return defaultProfileManagerFactory;
        }
    }

    public static SecurityLogic securityLogic(final SecurityLogic localLogic, final Config config, final SecurityLogic defaultLogic) {
        if (localLogic != null) {
            return localLogic;
        } else if (config != null && config.getSecurityLogic() != null) {
            return config.getSecurityLogic();
        } else {
            CommonHelper.assertNotNull("defaultLogic", defaultLogic);
            return defaultLogic;
        }
    }

    public static CallbackLogic callbackLogic(final CallbackLogic localLogic, final Config config, final CallbackLogic defaultLogic) {
        if (localLogic != null) {
            return localLogic;
        } else if (config != null && config.getCallbackLogic() != null) {
            return config.getCallbackLogic();
        } else {
            CommonHelper.assertNotNull("defaultLogic", defaultLogic);
            return defaultLogic;
        }
    }

    public static LogoutLogic logoutLogic(final LogoutLogic localLogic, final Config config, final LogoutLogic defaultLogic) {
        if (localLogic != null) {
            return localLogic;
        } else if (config != null && config.getLogoutLogic() != null) {
            return config.getLogoutLogic();
        } else {
            CommonHelper.assertNotNull("defaultLogic", defaultLogic);
            return defaultLogic;
        }
    }

    public static WebContextFactory webContextFactory(final WebContextFactory localFactory, final Config config,
                                                      final WebContextFactory defaultFactory) {
        if (localFactory != null) {
            return localFactory;
        } else if (config != null && config.getWebContextFactory() != null) {
            return config.getWebContextFactory();
        } else {
            CommonHelper.assertNotNull("defaultFactory", defaultFactory);
            return defaultFactory;
        }
    }
}
