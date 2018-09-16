package org.pac4j.oauth.profile.windowslive;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.Arrays;

/**
 * This class is the Windows Live profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveProfileDefinition extends OAuth20ProfileDefinition<WindowsLiveProfile, OAuth20Configuration> {

    public static final String NAME = "name";
    public static final String LAST_NAME = "last_name";
    public static final String LINK = "link";

    public WindowsLiveProfileDefinition() {
        super(x -> new WindowsLiveProfile());
        Arrays.stream(new String[] {NAME, LAST_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(LINK, Converters.URL);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return "https://apis.live.net/v5.0/me";
    }

    @Override
    public WindowsLiveProfile extractUserProfile(final String body) {
        final WindowsLiveProfile profile = newProfile();
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
