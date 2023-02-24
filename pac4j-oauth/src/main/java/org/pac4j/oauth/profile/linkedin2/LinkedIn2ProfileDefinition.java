package org.pac4j.oauth.profile.linkedin2;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the LinkedIn profile definition.
 *
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2ProfileDefinition extends OAuthProfileDefinition {
    /** Constant <code>LOCALIZED_FIRST_NAME="localizedFirstName"</code> */
    public static final String LOCALIZED_FIRST_NAME = "localizedFirstName";
    /** Constant <code>LOCALIZED_LAST_NAME="localizedLastName"</code> */
    public static final String LOCALIZED_LAST_NAME = "localizedLastName";
    /** Constant <code>PROFILE_PICTURE="profilePicture"</code> */
    public static final String PROFILE_PICTURE = "profilePicture";
    /** Constant <code>PROFILE_EMAILS="profileEmails"</code> */
    public static final String PROFILE_EMAILS = "profileEmails";

    /**
     * <p>Constructor for LinkedIn2ProfileDefinition.</p>
     */
    public LinkedIn2ProfileDefinition() {
        super(x -> new LinkedIn2Profile());
        Arrays.stream(new String[] {LOCALIZED_FIRST_NAME, LOCALIZED_LAST_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(PROFILE_PICTURE, new JsonConverter(LinkedIn2ProfilePicture.class));
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return ((LinkedIn2Configuration) configuration).getProfileUrl();
    }

    /** {@inheritDoc} */
    @Override
    public LinkedIn2Profile extractUserProfile(final String body) {
        val profile = (LinkedIn2Profile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json == null) {
            raiseProfileExtractionJsonError(body);
        }
        profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "id")));
        for (val attribute : getPrimaryAttributes()) {
            convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
        }
        return profile;
    }
}
