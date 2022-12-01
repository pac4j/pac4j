package org.pac4j.oauth.profile.figshare;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the FigShare profile definition
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class FigShareProfileDefinition extends OAuthProfileDefinition {
    public static final String LAST_NAME = "last_name";

    public FigShareProfileDefinition() {
        super(x -> new FigShareProfile());
        primary(LAST_NAME, Converters.STRING);
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://api.figshare.com/v2/account";
    }

    @Override
    public FigShareProfile extractUserProfile(final String body) {
        val profile = (FigShareProfile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            if (getProfileId() != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, getProfileId())));
            }
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
