package org.pac4j.core.profile.definition;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.factory.ProfileFactory;

/**
 * Profile definition with the common attributes.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CommonProfileDefinition extends ProfileDefinition {

    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first_name";
    public static final String FAMILY_NAME = "family_name";
    public static final String DISPLAY_NAME = "display_name";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";
    public static final String PICTURE_URL = "picture_url";
    public static final String PROFILE_URL = "profile_url";
    public static final String LOCATION = "location";

    public CommonProfileDefinition() {
        configurePrimaryAttributes();
    }

    protected void configurePrimaryAttributes() {
        primary(EMAIL, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GENDER, Converters.GENDER);
        primary(LOCALE, Converters.LOCALE);
        primary(PICTURE_URL, Converters.URL);
        primary(PROFILE_URL, Converters.URL);
        primary(LOCATION, Converters.STRING);
        primary(Pac4jConstants.USERNAME, Converters.STRING);
    }

    public CommonProfileDefinition(final ProfileFactory profileFactory) {
        this();
        setProfileFactory(profileFactory);
    }
}
