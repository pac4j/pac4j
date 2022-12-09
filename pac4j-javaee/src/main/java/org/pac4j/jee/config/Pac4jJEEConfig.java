package org.pac4j.jee.config;

import org.pac4j.core.config.Config;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jee.context.JEEContextFactory;
import org.pac4j.jee.context.session.JEESessionStoreFactory;
import org.pac4j.jee.http.adapter.JEEHttpActionAdapter;

/**
 * JEE specificities.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public final class Pac4jJEEConfig {

    /**
     * Apply the default JEE settings if they are not already defined in the configuration.
     *
     * @param config the config
     */
    public static void applyJEESettingsIfUndefined(final Config config) {
        CommonHelper.assertNotNull("config", config);
        config.setWebContextFactoryIfUndefined(JEEContextFactory.INSTANCE);
        config.setSessionStoreFactoryIfUndefined(JEESessionStoreFactory.INSTANCE);
        config.setHttpActionAdapterIfUndefined(JEEHttpActionAdapter.INSTANCE);
    }
}
