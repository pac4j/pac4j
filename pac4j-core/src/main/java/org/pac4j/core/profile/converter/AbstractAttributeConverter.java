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

    /**
     * <p>Constructor for AbstractAttributeConverter.</p>
     *
     * @param clazz a {@link Class} object
     */
    protected AbstractAttributeConverter(final Class<? extends Object> clazz) {
        this.clazz = clazz;
    }

    /** {@inheritDoc} */
    @Override
    public Object convert(final Object attribute) {
        Object t = null;
        if (attribute != null) {
            if (clazz.isAssignableFrom(attribute.getClass())) {
                t = attribute;
            } else if (attribute instanceof List l) {
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

    /**
     * <p>internalConvert.</p>
     *
     * @param attribute a {@link Object} object
     * @return a {@link Object} object
     */
    protected Object internalConvert(final Object attribute) {
        return null;
    }

    /**
     * <p>defaultValue.</p>
     *
     * @return a {@link Object} object
     */
    protected Object defaultValue() {
        return null;
    }

    /**
     * <p>accept.</p>
     *
     * @param typeName a {@link String} object
     * @return a {@link Boolean} object
     */
    public Boolean accept(final String typeName){
        return clazz.getSimpleName().equals(typeName);
    }
}
