package org.pac4j.core.adapter;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The JEE adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
public abstract class JEEAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JEEAdapter.class);

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
