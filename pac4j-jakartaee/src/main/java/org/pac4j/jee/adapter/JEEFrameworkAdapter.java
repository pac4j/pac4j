package org.pac4j.jee.adapter;

import jakarta.annotation.Priority;
import lombok.val;
import org.pac4j.core.adapter.DefaultFrameworkAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.jee.context.JEEContextFactory;
import org.pac4j.jee.context.session.JEESessionStoreFactory;
import org.pac4j.jee.http.adapter.JEEHttpActionAdapter;

/**
 * The JakartaEE framework adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
public class JEEFrameworkAdapter extends DefaultFrameworkAdapter {

    /** {@inheritDoc} */
    @Override
    public int compareManagers(final Object obj1, final Object obj2) {
        var p1 = 100;
        var p2 = 100;
        val p1a = obj1.getClass().getAnnotation(Priority.class);
        if (p1a != null) {
            p1 = p1a.value();
        }
        val p2a = obj2.getClass().getAnnotation(Priority.class);
        if (p2a != null) {
            p2 = p2a.value();
        }
        if (p1 < p2) {
            return -1;
        } else if (p1 > p2) {
            return 1;
        } else {
            return obj2.getClass().getSimpleName().compareTo(obj1.getClass().getSimpleName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void applyDefaultSettingsIfUndefined(final Config config) {
        super.applyDefaultSettingsIfUndefined(config);

        config.setWebContextFactoryIfUndefined(JEEContextFactory.INSTANCE);
        config.setSessionStoreFactoryIfUndefined(JEESessionStoreFactory.INSTANCE);
        config.setHttpActionAdapterIfUndefined(JEEHttpActionAdapter.INSTANCE);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "JakartaEE";
    }
}
