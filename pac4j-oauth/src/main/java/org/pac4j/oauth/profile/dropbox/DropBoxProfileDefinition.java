package org.pac4j.oauth.profile.dropbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
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
    public static final String SHARED = "shared";
    public static final String QUOTA = "quota";
    public static final String NORMAL = "normal";
    public static final String EMAIL_VERIFIED = "email_verified";

    public DropBoxProfileDefinition() {
        super(x -> new DropBoxProfile());
        primary(REFERRAL_LINK, Converters.STRING);
        primary(COUNTRY, Converters.LOCALE);
        primary(REFERRAL_LINK, Converters.URL);
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        secondary(SHARED, Converters.LONG);
        secondary(QUOTA, Converters.LONG);
        secondary(NORMAL, Converters.LONG);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken token, final OAuth20Configuration configuration) {
        return "https://api.dropbox.com/1/account/info";
    }

    @Override
    public DropBoxProfile extractUserProfile(final String body) {
        final DropBoxProfile profile = newProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "uid")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute));
            }
            json = (JsonNode) JsonHelper.getElement(json, "quota_info");
            if (json != null) {
                for (final String attribute : getSecondaryAttributes()) {
                    convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute));
                }
            }
        }
        return profile;
    }
}
