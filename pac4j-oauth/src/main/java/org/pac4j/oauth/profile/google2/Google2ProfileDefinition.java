package org.pac4j.oauth.profile.google2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.List;

/**
 * This class is the Google profile definition (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2ProfileDefinition extends OAuth20ProfileDefinition<Google2Profile, OAuth20Configuration> {

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

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return "https://www.googleapis.com/plus/v1/people/me";
    }

    @Override
    public Google2Profile extractUserProfile(final String body) {
        final Google2Profile profile = newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "id")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
