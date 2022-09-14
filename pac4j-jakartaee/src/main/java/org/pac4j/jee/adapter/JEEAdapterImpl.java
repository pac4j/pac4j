package org.pac4j.jee.adapter;

import jakarta.annotation.Priority;
import org.pac4j.core.adapter.JEEAdapter;

/**
 * The JakartaEE adapter implementation.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
public class JEEAdapterImpl extends JEEAdapter {

    @Override
    public int compareManagers(Object obj1, Object obj2) {
        var p1 = 100;
        var p2 = 100;
        final var p1a = obj1.getClass().getAnnotation(Priority.class);
        if (p1a != null) {
            p1 = p1a.value();
        }
        final var p2a = obj2.getClass().getAnnotation(Priority.class);
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
}
