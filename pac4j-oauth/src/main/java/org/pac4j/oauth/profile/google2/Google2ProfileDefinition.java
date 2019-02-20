package org.pac4j.oauth.profile.google2;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Google profile definition (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2ProfileDefinition extends OAuth20ProfileDefinition<Google2Profile, OAuth20Configuration> {

    @Deprecated
    public static final String DISPLAY_NAME = "displayName";
    @Deprecated
    public static final String URL = "url";
    @Deprecated
    public static final String FAMILY_NAME = "name.familyName";
    @Deprecated
    public static final String EMAILS = "emails";
    @Deprecated
    public static final String LANGUAGE = "language";
    @Deprecated
    public static final String BIRTHDAY = "birthday";
    @Deprecated
    public static final String EMAIL = "email";

    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String GIVEN_NAME = "given_name";
    public static final String NAME = "name";
    public static final String PICTURE = "picture";
    public static final String PROFILE = "profile";

    public Google2ProfileDefinition() {
        super(x -> new Google2Profile());
        primary(CommonProfileDefinition.EMAIL, Converters.STRING);
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        primary(CommonProfileDefinition.FAMILY_NAME, Converters.STRING);
        primary(GENDER, Converters.GENDER);
        primary(GIVEN_NAME, Converters.STRING);
        primary(LOCALE, Converters.LOCALE);
        primary(NAME, Converters.STRING);
        primary(PICTURE, Converters.URL);
        primary(PROFILE, Converters.URL);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return "https://www.googleapis.com/oauth2/v3/userinfo";
    }

    @Override
    public Google2Profile extractUserProfile(final String body) {
        final Google2Profile profile = newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "sub")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
