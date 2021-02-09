package org.pac4j.oauth.profile.windowslive;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.Token;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.Arrays;

/**
 * This class is the Windows Live profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveProfileDefinition extends OAuthProfileDefinition {

    public static final String NAME = "name";
    public static final String LAST_NAME = "last_name";
    public static final String LINK = "link";

    public WindowsLiveProfileDefinition() {
        super(x -> new WindowsLiveProfile());
        Arrays.stream(new String[] {NAME, LAST_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(LINK, Converters.URL);
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://apis.live.net/v5.0/me";
    }

    @Override
    public WindowsLiveProfile extractUserProfile(final String body) {
        final var profile = (WindowsLiveProfile) newProfile();
        final var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "id")));
            for (final var attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
