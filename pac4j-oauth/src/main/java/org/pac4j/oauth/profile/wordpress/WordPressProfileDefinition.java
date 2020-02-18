package org.pac4j.oauth.profile.wordpress;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

/**
 * This class is the WordPress profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfileDefinition extends OAuth20ProfileDefinition<WordPressProfile, OAuth20Configuration> {

    public static final String PRIMARY_BLOG = "primary_blog";
    public static final String AVATAR_URL = "avatar_URL";
    public static final String PROFILE_URL = "profile_URL";
    public static final String LINKS = "links";

    public WordPressProfileDefinition() {
        super(x -> new WordPressProfile());
        primary(Pac4jConstants.USERNAME, Converters.STRING);
        primary(PRIMARY_BLOG, Converters.INTEGER);
        primary(AVATAR_URL, Converters.URL);
        primary(PROFILE_URL, Converters.URL);
        secondary(LINKS, new JsonConverter<>(WordPressLinks.class));
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return "https://public-api.wordpress.com/rest/v1/me/?pretty=1";
    }

    @Override
    public WordPressProfile extractUserProfile(final String body) {
        final WordPressProfile profile = newProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "ID")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            json = json.get("meta");
            if (json != null) {
                final String attribute = WordPressProfileDefinition.LINKS;
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
