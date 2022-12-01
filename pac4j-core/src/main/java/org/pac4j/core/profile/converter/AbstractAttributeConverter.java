package org.pac4j.core.profile.converter;

import lombok.val;

import java.util.List;

/**
 * This abstract attribute converter handles some common behaviors for simple type converters.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractAttributeConverter implements AttributeConverter {

    private final Class<? extends Object> clazz;

    protected AbstractAttributeConverter(final Class<? extends Object> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object convert(final Object attribute) {
        Object t = null;
        if (attribute != null) {
            if (clazz.isAssignableFrom(attribute.getClass())) {
                t = attribute;
            } else if (attribute instanceof List) {
                val l = (List) attribute;
                if (l.size() > 0) {
                    val element = l.get(0);
                    if (clazz.isAssignableFrom(element.getClass())) {
                        t = element;
                    }else {
                        t = internalConvert(element);
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

    protected Object internalConvert(final Object attribute) {
        return null;
    }

    protected Object defaultValue() {
        return null;
    }

    public Boolean accept(final String typeName){
        return clazz.getSimpleName().equals(typeName);
    }
}
