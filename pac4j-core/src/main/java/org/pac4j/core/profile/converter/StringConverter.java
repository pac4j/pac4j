package org.pac4j.core.profile.converter;


/**
 * This class only keeps String objects.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringConverter implements AttributeConverter<String> {
    
    @Override
    public String convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            return (String) attribute;
        }
        return null;
    }
}
