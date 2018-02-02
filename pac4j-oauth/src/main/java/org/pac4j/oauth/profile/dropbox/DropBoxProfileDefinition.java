package org.pac4j.oauth.profile.dropbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

/**
 * This class is the DropBox profile definition.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfileDefinition extends OAuth20ProfileDefinition<DropBoxProfile, OAuth20Configuration> {

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
    public String getProfileUrl(final OAuth2AccessToken token, final OAuth20Configuration configuration) {
        return "https://api.dropboxapi.com/2/users/get_current_account";
    }

    @Override
    public Verb getProfileVerb() {
        return Verb.POST;
    }

    @Override
    public DropBoxProfile extractUserProfile(final String body) {
        final DropBoxProfile profile = newProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "account_id")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute));
            }
            json = (JsonNode) JsonHelper.getElement(json, "name");
            if (json != null) {
                convertAndAdd(profile, FIRST_NAME, JsonHelper.getElement(json, "familiar_name"));
                convertAndAdd(profile, FAMILY_NAME, JsonHelper.getElement(json, "surname"));
                convertAndAdd(profile, DISPLAY_NAME, JsonHelper.getElement(json, "display_name"));
            }
        }
        return profile;
    }
}
