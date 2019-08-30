package org.pac4j.oauth.profile.linkedin2;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.Arrays;

/**
 * This class is the LinkedIn profile definition.
 *
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2ProfileDefinition extends OAuth20ProfileDefinition<LinkedIn2Profile, LinkedIn2Configuration> {
    public static final String LOCALIZED_FIRST_NAME = "localizedFirstName";
    public static final String LOCALIZED_LAST_NAME = "localizedLastName";
    public static final String PROFILE_PICTURE = "profilePicture";
    public static final String PROFILE_EMAILS = "profileEmails";

    public LinkedIn2ProfileDefinition() {
        super(x -> new LinkedIn2Profile());
        Arrays.stream(new String[] {LOCALIZED_FIRST_NAME, LOCALIZED_LAST_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(PROFILE_PICTURE, new JsonConverter<LinkedIn2ProfilePicture>(LinkedIn2ProfilePicture.class));
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final LinkedIn2Configuration configuration) {
        return "https://api.linkedin.com/v2/me" + "?projection=(id," + LOCALIZED_FIRST_NAME + "," + LOCALIZED_LAST_NAME + ","
                + PROFILE_PICTURE + "(displayImage~:playableStreams))";
    }

    @Override
    public LinkedIn2Profile extractUserProfile(final String body) {
        LinkedIn2Profile profile = newProfile();
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
