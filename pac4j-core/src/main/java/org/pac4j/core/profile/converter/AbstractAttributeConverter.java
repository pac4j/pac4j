package org.pac4j.core.profile.converter;

/**
 * This abstract attribute converter handles somme common behaviors.
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
        if (attribute != null) {
            if (clazz.isAssignableFrom(attribute.getClass())) {
                return (T) attribute;
            } else {
                final T t = internalConvert(attribute);
                if (t != null) {
                    return t;
                }
            }
        }
        return defaultValue();
    }

    protected T internalConvert(final Object attribute) {
        return null;
    }

    protected T defaultValue() {
        return null;
    }
}
