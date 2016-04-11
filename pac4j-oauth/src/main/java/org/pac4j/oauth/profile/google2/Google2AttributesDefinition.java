package org.pac4j.oauth.profile.google2;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;

/**
 * This class defines the attributes of the Google profile (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2AttributesDefinition extends AttributesDefinition {

    public static final String GENDER = "gender";
    public static final String DISPLAY_NAME = "displayName";
    public static final String GIVEN_NAME = "name.givenName";
    public static final String FAMILY_NAME = "name.familyName";
    public static final String URL = "url";
    public static final String PICTURE = "image.url";
    public static final String LANGUAGE = "language";
    public static final String BIRTHDAY = "birthday";
    public static final String EMAILS = "emails";

    public Google2AttributesDefinition() {
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GIVEN_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(URL, Converters.STRING);
        primary(PICTURE, Converters.STRING);
        primary(GENDER, Converters.GENDER);
        primary(LANGUAGE, Converters.LOCALE);
        primary(BIRTHDAY, new FormattedDateConverter("yyyy-MM-dd"));
        primary(EMAILS, new JsonListConverter(Google2Email.class, Google2Email[].class));
    }
}
