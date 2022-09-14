package org.pac4j.core.adapter;

/**
 * Default JEE adapter.
 *
 * @author Jerome LELEU
 * @since 5.6.0
 */
public class DefaultJEEAdapter extends JEEAdapter {

    @Override
    public int compareManagers(final Object obj1, final Object obj2) {
        if (obj1 != null && obj2 != null) {
            return obj2.getClass().getSimpleName().compareTo(obj1.getClass().getSimpleName());
        } else {
            return 0;
        }
    }
}
