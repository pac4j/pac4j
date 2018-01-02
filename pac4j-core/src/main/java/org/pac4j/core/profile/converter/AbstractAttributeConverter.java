package org.pac4j.core.profile.converter;

import java.util.List;

/**
 * This abstract attribute converter handles some common behaviors for simple type converters.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractAttributeConverter<T extends Object> implements AttributeConverter<T> {

    private final Class<T> clazz;

    protected AbstractAttributeConverter(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convert(final Object attribute) {
        T t = null;
        if (attribute != null) {
            if (clazz.isAssignableFrom(attribute.getClass())) {
                t = (T) attribute;
            } else if (attribute instanceof List) {
                final List l = (List) attribute;
                if (l.size() > 0) {
                    final Object element = l.get(0);
                    if (clazz.isAssignableFrom(element.getClass())) {
                        t = (T) element;
                    }
                }
            } else {
                t = internalConvert(attribute);
            }
        }
        if (t != null) {
            return t;
        } else {
            return defaultValue();
        }
    }

    protected T internalConvert(final Object attribute) {
        return null;
    }

    protected T defaultValue() {
        return null;
    }

    public Boolean accept(final String typeName){
        return clazz.getSimpleName().equals(typeName);
    }
}
