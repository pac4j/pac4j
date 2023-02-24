package org.pac4j.oauth.profile.wordpress;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the WordPress profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>PRIMARY_BLOG="primary_blog"</code> */
    public static final String PRIMARY_BLOG = "primary_blog";
    /** Constant <code>AVATAR_URL="avatar_URL"</code> */
    public static final String AVATAR_URL = "avatar_URL";
    /** Constant <code>PROFILE_URL="profile_URL"</code> */
    public static final String PROFILE_URL = "profile_URL";
    /** Constant <code>LINKS="links"</code> */
    public static final String LINKS = "links";

    /**
     * <p>Constructor for WordPressProfileDefinition.</p>
     */
    public WordPressProfileDefinition() {
        super(x -> new WordPressProfile());
        primary(Pac4jConstants.USERNAME, Converters.STRING);
        primary(PRIMARY_BLOG, Converters.INTEGER);
        primary(AVATAR_URL, Converters.URL);
        primary(PROFILE_URL, Converters.URL);
        secondary(LINKS, new JsonConverter(WordPressLinks.class));
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://public-api.wordpress.com/rest/v1/me/?pretty=1";
    }

    /** {@inheritDoc} */
    @Override
    public WordPressProfile extractUserProfile(final String body) {
        val profile = (WordPressProfile) newProfile();
        var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "ID")));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            json = json.get("meta");
            if (json != null) {
                val attribute = WordPressProfileDefinition.LINKS;
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
