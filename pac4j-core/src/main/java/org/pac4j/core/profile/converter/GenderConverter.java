package org.pac4j.core.profile.converter;

import org.pac4j.core.profile.Gender;

/**
 * This class converts a String to a Gender.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverter implements AttributeConverter<Gender> {
    
    @Override
    public Gender convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof String) {
                final String s = ((String) attribute).toLowerCase();
                if ("m".equals(s) || "male".equals(s)) {
                    return Gender.MALE;
                } else if ("f".equals(s) || "female".equals(s)) {
                    return Gender.FEMALE;
                } else {
                    return Gender.UNSPECIFIED;
                }
                // for Vk:
            } else if (attribute instanceof Integer) {
                Integer value = (Integer) attribute;
                if (value == 2) {
                    return Gender.MALE;
                } else if (value == 1) {
                    return Gender.FEMALE;
                } else {
                    return Gender.UNSPECIFIED;
                }
            } else if (attribute instanceof Gender) {
                return (Gender) attribute;
            }
        }
        return null;
    }
}
