package org.pac4j.oauth.profile.dropbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

/**
 * This class is the DropBox profile definition.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfileDefinition extends OAuthProfileDefinition {

    public static final String REFERRAL_LINK = "referral_link";
    public static final String COUNTRY = "country";
    public static final String EMAIL_VERIFIED = "email_verified";

    public DropBoxProfileDefinition() {
        super(x -> new DropBoxProfile());
        primary(REFERRAL_LINK, Converters.STRING);
        primary(COUNTRY, Converters.LOCALE);
        primary(REFERRAL_LINK, Converters.URL);
        primary(EMAIL, Converters.STRING);
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
    }

    @Override
    public String getProfileUrl(final Token token, final OAuthConfiguration configuration) {
        return "https://api.dropboxapi.com/2/users/get_current_account";
    }

    @Override
    public Verb getProfileVerb() {
        return Verb.POST;
    }

    @Override
    public DropBoxProfile extractUserProfile(final String body) {
        final DropBoxProfile profile = (DropBoxProfile) newProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "account_id")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            json = (JsonNode) JsonHelper.getElement(json, "name");
            if (json != null) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, FIRST_NAME, JsonHelper.getElement(json, "familiar_name"));
                convertAndAdd(profile, PROFILE_ATTRIBUTE, FAMILY_NAME, JsonHelper.getElement(json, "surname"));
                convertAndAdd(profile, PROFILE_ATTRIBUTE, DISPLAY_NAME, JsonHelper.getElement(json, "display_name"));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
