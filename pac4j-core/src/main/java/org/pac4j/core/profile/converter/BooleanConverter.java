package org.pac4j.core.profile.converter;

/**
 * This class converts a String into a Boolean or returns the Boolean in input.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class BooleanConverter implements AttributeConverter<Boolean> {
    
    @Override
    public Boolean convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof Boolean) {
                return (Boolean) attribute;
            } 
            if (attribute instanceof String) {
                return Boolean.parseBoolean((String) attribute);
            }
        }
        return Boolean.FALSE;
    }
}
