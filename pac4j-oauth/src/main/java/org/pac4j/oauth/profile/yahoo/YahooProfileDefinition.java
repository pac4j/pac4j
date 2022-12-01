package org.pac4j.oauth.profile.yahoo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;
import java.util.List;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Yahoo profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class YahooProfileDefinition extends OAuthProfileDefinition {

    public static final String ABOUT_ME = "aboutMe";
    public static final String AGE_CATEGORY = "ageCategory";
    public static final String ADDRESSES = "addresses";
    public static final String BIRTH_YEAR = "birthYear";
    public static final String BIRTHDATE = "birthdate";
    public static final String CREATED = "created";
    public static final String DISPLAY_AGE = "displayAge";
    public static final String DISCLOSURES = "disclosures";
    public static final String EMAILS = "emails";
    public static final String FAMILY_NAME = "familyName";
    public static final String GIVEN_NAME = "givenName";
    public static final String IMAGE = "image";
    public static final String INTERESTS = "interests";
    public static final String IS_CONNECTED = "isConnected";
    public static final String LANG = "lang";
    public static final String MEMBER_SINCE = "memberSince";
    public static final String NICKNAME = "nickname";
    public static final String PROFILE_URL = "profileUrl";
    public static final String TIME_ZONE = "timeZone";
    public static final String UPDATED = "updated";
    public static final String URI = "uri";

    public YahooProfileDefinition() {
        super(x -> new YahooProfile());
        Arrays.stream(new String[] {ABOUT_ME, FAMILY_NAME, GIVEN_NAME, NICKNAME, TIME_ZONE, URI, AGE_CATEGORY})
                .forEach(a -> primary(a, Converters.STRING));
        primary(IS_CONNECTED, Converters.BOOLEAN);
        primary(BIRTH_YEAR, Converters.INTEGER);
        primary(LANG, Converters.LOCALE);
        primary(DISPLAY_AGE, Converters.INTEGER);
        primary(BIRTHDATE, new DateConverter("MM/dd"));
        primary(ADDRESSES, new JsonConverter(List.class, new TypeReference<List<YahooAddress>>() {}));
        primary(DISCLOSURES, new JsonConverter(List.class, new TypeReference<List<YahooDisclosure>>() {}));
        primary(EMAILS, new JsonConverter(List.class, new TypeReference<List<YahooEmail>>() {}));
        primary(IMAGE, new JsonConverter(YahooImage.class));
        primary(INTERESTS, new JsonConverter(List.class, new TypeReference<List<YahooInterest>>() {}));
        primary(CREATED, Converters.DATE_TZ_RFC822);
        primary(MEMBER_SINCE, Converters.DATE_TZ_RFC822);
        primary(UPDATED, Converters.DATE_TZ_RFC822);
        primary(PROFILE_URL, Converters.URL);
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://social.yahooapis.com/v1/me/guid?format=xml";
    }

    @Override
    public YahooProfile extractUserProfile(final String body) {
        val profile = (YahooProfile) newProfile();
        var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get("profile");
            if (json != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "guid")));
                for (val attribute : getPrimaryAttributes()) {
                    convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
                }
            } else {
                raiseProfileExtractionJsonError(body, "profile");
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
