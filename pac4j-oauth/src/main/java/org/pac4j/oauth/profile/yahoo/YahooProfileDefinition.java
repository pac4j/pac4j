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

    /** Constant <code>ABOUT_ME="aboutMe"</code> */
    public static final String ABOUT_ME = "aboutMe";
    /** Constant <code>AGE_CATEGORY="ageCategory"</code> */
    public static final String AGE_CATEGORY = "ageCategory";
    /** Constant <code>ADDRESSES="addresses"</code> */
    public static final String ADDRESSES = "addresses";
    /** Constant <code>BIRTH_YEAR="birthYear"</code> */
    public static final String BIRTH_YEAR = "birthYear";
    /** Constant <code>BIRTHDATE="birthdate"</code> */
    public static final String BIRTHDATE = "birthdate";
    /** Constant <code>CREATED="created"</code> */
    public static final String CREATED = "created";
    /** Constant <code>DISPLAY_AGE="displayAge"</code> */
    public static final String DISPLAY_AGE = "displayAge";
    /** Constant <code>DISCLOSURES="disclosures"</code> */
    public static final String DISCLOSURES = "disclosures";
    /** Constant <code>EMAILS="emails"</code> */
    public static final String EMAILS = "emails";
    /** Constant <code>FAMILY_NAME="familyName"</code> */
    public static final String FAMILY_NAME = "familyName";
    /** Constant <code>GIVEN_NAME="givenName"</code> */
    public static final String GIVEN_NAME = "givenName";
    /** Constant <code>IMAGE="image"</code> */
    public static final String IMAGE = "image";
    /** Constant <code>INTERESTS="interests"</code> */
    public static final String INTERESTS = "interests";
    /** Constant <code>IS_CONNECTED="isConnected"</code> */
    public static final String IS_CONNECTED = "isConnected";
    /** Constant <code>LANG="lang"</code> */
    public static final String LANG = "lang";
    /** Constant <code>MEMBER_SINCE="memberSince"</code> */
    public static final String MEMBER_SINCE = "memberSince";
    /** Constant <code>NICKNAME="nickname"</code> */
    public static final String NICKNAME = "nickname";
    /** Constant <code>PROFILE_URL="profileUrl"</code> */
    public static final String PROFILE_URL = "profileUrl";
    /** Constant <code>TIME_ZONE="timeZone"</code> */
    public static final String TIME_ZONE = "timeZone";
    /** Constant <code>UPDATED="updated"</code> */
    public static final String UPDATED = "updated";
    /** Constant <code>URI="uri"</code> */
    public static final String URI = "uri";

    /**
     * <p>Constructor for YahooProfileDefinition.</p>
     */
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

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://social.yahooapis.com/v1/me/guid?format=xml";
    }

    /** {@inheritDoc} */
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
