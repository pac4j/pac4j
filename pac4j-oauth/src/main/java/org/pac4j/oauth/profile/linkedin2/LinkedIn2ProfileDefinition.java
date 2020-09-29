package org.pac4j.oauth.profile.linkedin2;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.Token;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.Arrays;

/**
 * This class is the LinkedIn profile definition.
 *
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2ProfileDefinition extends OAuthProfileDefinition {
    public static final String LOCALIZED_FIRST_NAME = "localizedFirstName";
    public static final String LOCALIZED_LAST_NAME = "localizedLastName";
    public static final String PROFILE_PICTURE = "profilePicture";
    public static final String PROFILE_EMAILS = "profileEmails";

    public LinkedIn2ProfileDefinition() {
        super(x -> new LinkedIn2Profile());
        Arrays.stream(new String[] {LOCALIZED_FIRST_NAME, LOCALIZED_LAST_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(PROFILE_PICTURE, new JsonConverter(LinkedIn2ProfilePicture.class));
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return ((LinkedIn2Configuration) configuration).getProfileUrl();
    }

    @Override
    public LinkedIn2Profile extractUserProfile(final String body) {
        final LinkedIn2Profile profile = (LinkedIn2Profile) newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json == null) {
            raiseProfileExtractionJsonError(body);
        }
        profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "id")));
        for (final String attribute : getPrimaryAttributes()) {
            convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
        }
        return profile;
    }
}
