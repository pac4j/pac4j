package org.pac4j.core.profile.converter;

import org.pac4j.core.profile.Gender;

/**
 * This class converts a String to a Gender.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverter implements AttributeConverter<Gender> {
    
    private final String maleText;
    
    private final String femaleText;
    
    public GenderConverter(final String maleText, final String femaleText) {
        this.maleText = maleText;
        this.femaleText = femaleText;
    }
    
    @Override
    public Gender convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof String) {
                final String s = ((String) attribute).toLowerCase();
                if (s.equals(this.maleText) || Gender.MALE.toString().toLowerCase().equals(s)) {
                    return Gender.MALE;
                } else if (s.equals(this.femaleText) || Gender.FEMALE.toString().toLowerCase().equals(s)) {
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
