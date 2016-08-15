package org.pac4j.oauth.profile.generic;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * Default attribute definition of the GenericOAuth20Profile
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericAttributesDefinition extends AttributesDefinition {

    public final String EMAIL = "email";
    public final String FIRST_NAME = "first_name";
    public final String FAMILY_NAME = "family_name";
    public final String DISPLAY_NAME = "display_name";
    public final String GENDER = "gender";
    public final String LOCALE = "locale";
    public final String PICTURE_URL = "picture_url";
    public final String PROFILE_URL = "profile_url";
    public final String LOCATION = "location";

    public GenericAttributesDefinition() {
        primary(EMAIL, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GENDER, Converters.GENDER);
        primary(LOCALE, Converters.LOCALE);
        primary(PICTURE_URL, Converters.URL);
        primary(PROFILE_URL, Converters.URL);
        primary(EMAIL, Converters.STRING);
    }
}