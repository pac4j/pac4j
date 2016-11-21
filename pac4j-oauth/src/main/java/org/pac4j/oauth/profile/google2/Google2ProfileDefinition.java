package org.pac4j.oauth.profile.google2;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.List;

/**
 * This class is the Google profile definition (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2ProfileDefinition extends CommonProfileDefinition<Google2Profile> {

    public static final String GENDER = "gender";
    public static final String DISPLAY_NAME = "displayName";
    public static final String GIVEN_NAME = "name.givenName";
    public static final String FAMILY_NAME = "name.familyName";
    public static final String URL = "url";
    public static final String PICTURE = "image.url";
    public static final String LANGUAGE = "language";
    public static final String BIRTHDAY = "birthday";
    public static final String EMAILS = "emails";

    public Google2ProfileDefinition() {
        super(x -> new Google2Profile());
        primary(DISPLAY_NAME, Converters.STRING);
        primary(GIVEN_NAME, Converters.STRING);
        primary(FAMILY_NAME, Converters.STRING);
        primary(URL, Converters.URL);
        primary(PICTURE, Converters.URL);
        primary(LANGUAGE, Converters.LOCALE);
        primary(BIRTHDAY, new DateConverter("yyyy-MM-dd"));
        primary(EMAILS, new JsonConverter(List.class, new TypeReference<List<Google2Email>>() {}));
    }
}
