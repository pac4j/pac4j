package org.pac4j.core.adapter;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * The JEE adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
public abstract class JEEAdapter {

    public static final JEEAdapter INSTANCE;

    static {
        try {
            INSTANCE = (JEEAdapter) CommonHelper.getConstructor("org.pac4j.jee.adapter.JEEAdapterImpl").newInstance();
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                       InvocationTargetException | InstantiationException e) {
            throw new TechnicalException(e);
        }
    }

    public abstract int compareManagers(final Object obj1, final Object obj2);
}
