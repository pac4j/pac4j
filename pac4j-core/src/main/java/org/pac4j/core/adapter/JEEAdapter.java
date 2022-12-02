package org.pac4j.core.adapter;

import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The JEE adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
@Slf4j
public abstract class JEEAdapter {

    public static final JEEAdapter INSTANCE;

    static {
        Constructor constructor = null;
        try {
            constructor = CommonHelper.getConstructor("org.pac4j.jee.adapter.JEEAdapterImpl");
        } catch (final ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.debug("Cannot find a JEE adapter: {}", e.getMessage());
        }
        if (constructor == null) {
            LOGGER.warn("No JEEAdapterImpl found. Using DefaultJEEAdapter...");
            INSTANCE = new DefaultJEEAdapter();
        } else {
            try {
                INSTANCE = (JEEAdapter) constructor.newInstance();
                LOGGER.info("Using JEE adapter: {}", INSTANCE);
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new TechnicalException(e);
            }
        }
    }

    public abstract int compareManagers(final Object obj1, final Object obj2);
}
