package org.pac4j.oauth.profile.bitbucket;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Bitbucket profile definition.
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfileDefinition extends OAuthProfileDefinition {

    public static final String LAST_NAME = "last_name";
    public static final String IS_TEAM = "is_team";
    public static final String AVATAR = "avatar";
    public static final String RESOURCE_URI = "resource_uri";

    public BitbucketProfileDefinition() {
        super(x -> new BitbucketProfile());
        Arrays.stream(new String[] {Pac4jConstants.USERNAME, LAST_NAME})
                .forEach(a -> primary(a, Converters.STRING));
        primary(IS_TEAM, Converters.BOOLEAN);
        primary(AVATAR, Converters.URL);
        primary(RESOURCE_URI, Converters.URL);
    }

    @Override
    public String getProfileUrl(final Token token, final OAuthConfiguration configuration) {
        return "https://bitbucket.org/api/1.0/user/";
    }

    @Override
    public BitbucketProfile extractUserProfile(final String body) {
        val profile = (BitbucketProfile) newProfile();
        var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = (JsonNode) JsonHelper.getElement(json, "user");
            if (json != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, Pac4jConstants.USERNAME)));
                for (val attribute : getPrimaryAttributes()) {
                    convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
                }
            } else {
                raiseProfileExtractionJsonError(body, "user");
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
