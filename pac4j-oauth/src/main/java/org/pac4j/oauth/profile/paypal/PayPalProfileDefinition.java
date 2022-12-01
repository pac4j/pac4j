package org.pac4j.oauth.profile.paypal;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the PayPal profile definition.
 *
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalProfileDefinition extends OAuthProfileDefinition {

    public static final String ADDRESS = "address";
    public static final String LANGUAGE = "language";
    public static final String ZONEINFO = "zoneinfo";
    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";

    public PayPalProfileDefinition() {
        super(x -> new PayPalProfile());
        Arrays.stream(new String[] {ZONEINFO, NAME, GIVEN_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(ADDRESS, new JsonConverter(PayPalAddress.class));
        primary(LANGUAGE, Converters.LOCALE);
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://api.paypal.com/v1/identity/openidconnect/userinfo?schema=openid";
    }

    @Override
    public PayPalProfile extractUserProfile(final String body) {
        val profile = (PayPalProfile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            val userId = (String) JsonHelper.getElement(json, "user_id");
            profile.setId(CommonHelper.substringAfter(userId, "/user/"));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
