package org.pac4j.oauth.profile.figshare;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import com.github.scribejava.core.model.Token;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

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
        final FigShareProfile profile = (FigShareProfile) newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            if (getProfileId() != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, getProfileId())));
            }
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
