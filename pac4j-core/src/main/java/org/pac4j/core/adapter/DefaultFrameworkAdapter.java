package org.pac4j.core.adapter;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.CommonHelper;

/**
 * Default framework adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
public class DefaultFrameworkAdapter extends FrameworkAdapter {

    @Override
    public int compareManagers(final Object obj1, final Object obj2) {
        if (obj1 != null && obj2 != null) {
            return obj2.getClass().getSimpleName().compareTo(obj1.getClass().getSimpleName());
        } else {
            return 0;
        }
    }

    @Override
    public void applyDefaultSettingsIfUndefined(final Config config) {
        CommonHelper.assertNotNull("config", config);

        config.setSecurityLogicIfUndefined(DefaultSecurityLogic.INSTANCE);
        config.setCallbackLogicIfUndefined(DefaultCallbackLogic.INSTANCE);
        config.setLogoutLogicIfUndefined(DefaultLogoutLogic.INSTANCE);
        config.setProfileManagerFactoryIfUndefined(ProfileManagerFactory.DEFAULT);
    }

    @Override
    public String toString() {
        return "default";
    }
}
