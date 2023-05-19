package org.pac4j.core.profile.definition;

import org.pac4j.core.profile.converter.ChainingConverter;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.factory.ProfileFactory;

import java.util.List;

/**
 * Profile definition with the common attributes.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CommonProfileDefinition extends ProfileDefinition {

    /** Constant <code>EMAIL="email"</code> */
    public static final String EMAIL = "email";
    /** Constant <code>FIRST_NAME="first_name"</code> */
    public static final String FIRST_NAME = "first_name";
    /** Constant <code>FAMILY_NAME="family_name"</code> */
    public static final String FAMILY_NAME = "family_name";
    /** Constant <code>DISPLAY_NAME="display_name"</code> */
    public static final String DISPLAY_NAME = "display_name";
    /** Constant <code>GENDER="gender"</code> */
    public static final String GENDER = "gender";
    /** Constant <code>LOCALE="locale"</code> */
    public static final String LOCALE = "locale";
    /** Constant <code>PICTURE_URL="picture_url"</code> */
    public static final String PICTURE_URL = "picture_url";
    /** Constant <code>PROFILE_URL="profile_url"</code> */
    public static final String PROFILE_URL = "profile_url";
    /** Constant <code>LOCATION="location"</code> */
    public static final String LOCATION = "location";

    /**
     * <p>Constructor for CommonProfileDefinition.</p>
     */
    public CommonProfileDefinition() {
        configurePrimaryAttributes();
    }

    /**
     * <p>configurePrimaryAttributes.</p>
     */
    protected void configurePrimaryAttributes() {
        primary(EMAIL, Converters.STRING);
        primary(FIRST_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GENDER, new ChainingConverter(List.of(Converters.GENDER, Converters.STRING)));
        primary(LOCALE, new ChainingConverter(List.of(Converters.LOCALE, Converters.STRING)));
        primary(PICTURE_URL, new ChainingConverter(List.of(Converters.URL, Converters.STRING)));
        primary(PROFILE_URL, new ChainingConverter(List.of(Converters.URL, Converters.STRING)));
        primary(LOCATION, Converters.STRING);
        primary(Pac4jConstants.USERNAME, Converters.STRING);
    }

    /**
     * <p>Constructor for CommonProfileDefinition.</p>
     *
     * @param profileFactory a {@link ProfileFactory} object
     */
    public CommonProfileDefinition(final ProfileFactory profileFactory) {
        this();
        setProfileFactory(profileFactory);
    }
}
