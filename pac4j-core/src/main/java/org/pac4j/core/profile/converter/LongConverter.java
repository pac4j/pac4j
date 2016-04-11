package org.pac4j.core.profile.converter;

/**
 * This class converts a String into a Long or returns the Long (or Integer) in input.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class LongConverter implements AttributeConverter<Long> {
    
    @Override
    public Long convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof Integer) {
                return Long.valueOf((Integer) attribute);
            } else if (attribute instanceof Long) {
                return (Long) attribute;
            } else if (attribute instanceof String) {
                return Long.parseLong((String) attribute);
            }
        }
        return null;
    }
}
