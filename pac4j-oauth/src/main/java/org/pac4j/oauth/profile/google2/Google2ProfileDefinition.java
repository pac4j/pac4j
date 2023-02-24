package org.pac4j.oauth.profile.google2;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Google profile definition (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2ProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>EMAIL_VERIFIED="email_verified"</code> */
    public static final String EMAIL_VERIFIED = "email_verified";
    /** Constant <code>GIVEN_NAME="given_name"</code> */
    public static final String GIVEN_NAME = "given_name";
    /** Constant <code>NAME="name"</code> */
    public static final String NAME = "name";
    /** Constant <code>PICTURE="picture"</code> */
    public static final String PICTURE = "picture";
    /** Constant <code>PROFILE="profile"</code> */
    public static final String PROFILE = "profile";

    /**
     * <p>Constructor for Google2ProfileDefinition.</p>
     */
    public Google2ProfileDefinition() {
        super(x -> new Google2Profile());
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        primary(GIVEN_NAME, Converters.STRING);
        primary(NAME, Converters.STRING);
        primary(PICTURE, Converters.URL);
        primary(PROFILE, Converters.URL);
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://www.googleapis.com/oauth2/v3/userinfo";
    }

    /** {@inheritDoc} */
    @Override
    public Google2Profile extractUserProfile(final String body) {
        val profile = (Google2Profile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "sub")));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
