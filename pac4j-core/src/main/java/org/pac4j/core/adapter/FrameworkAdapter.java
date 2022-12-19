package org.pac4j.core.adapter;

import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The framework adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
@Slf4j
public abstract class FrameworkAdapter {

    public static final FrameworkAdapter INSTANCE;

    static {
        Constructor constructor = null;

        try {
            constructor = CommonHelper.getConstructor("org.pac4j.framework.adapter.FrameworkAdapterImpl");
        } catch (final ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.debug("Cannot find a framework adapter: {}", e.getMessage());
        }

        if (constructor == null) {
            try {
                constructor = CommonHelper.getConstructor("org.pac4j.jee.adapter.JEEFrameworkAdapter");
            } catch (final ClassNotFoundException | NoSuchMethodException e) {
                LOGGER.debug("Cannot find a JEE framework adapter: {}", e.getMessage());
            }
        }

        if (constructor == null) {
            LOGGER.warn("No framework adapter found. Using DefaultFrameworkAdapter...");
            INSTANCE = new DefaultFrameworkAdapter();
        } else {
            try {
                INSTANCE = (FrameworkAdapter) constructor.newInstance();
                LOGGER.info("Using {} framework adapter", INSTANCE);
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new TechnicalException(e);
            }
        }
    }

    public abstract int compareManagers(final Object obj1, final Object obj2);

    public abstract void applyDefaultSettingsIfUndefined(final Config config);
}
