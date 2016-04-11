package org.pac4j.oauth.profile.yahoo;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the Yahoo profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class YahooAttributesDefinition extends AttributesDefinition {
    
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
    public static final String GENDER = "gender";
    public static final String GIVEN_NAME = "givenName";
    public static final String IMAGE = "image";
    public static final String INTERESTS = "interests";
    public static final String IS_CONNECTED = "isConnected";
    public static final String LANG = "lang";
    public static final String LOCATION = "location";
    public static final String MEMBER_SINCE = "memberSince";
    public static final String NICKNAME = "nickname";
    public static final String PROFILE_URL = "profileUrl";
    public static final String TIME_ZONE = "timeZone";
    public static final String UPDATED = "updated";
    public static final String URI = "uri";
    
    public YahooAttributesDefinition() {
        Arrays.stream(new String[] {ABOUT_ME, FAMILY_NAME, GIVEN_NAME, LOCATION, NICKNAME, PROFILE_URL, TIME_ZONE, URI, AGE_CATEGORY})
                .forEach(a -> primary(a, Converters.STRING));
        primary(IS_CONNECTED, Converters.BOOLEAN);
        primary(BIRTH_YEAR, Converters.INTEGER);
        primary(LANG, Converters.LOCALE);
        primary(DISPLAY_AGE, Converters.INTEGER);
        primary(BIRTHDATE, new FormattedDateConverter("MM/dd"));
        primary(ADDRESSES, new JsonListConverter(YahooAddress.class, YahooAddress[].class));
        primary(DISCLOSURES, new JsonListConverter(YahooDisclosure.class, YahooDisclosure[].class));
        primary(EMAILS, new JsonListConverter(YahooEmail.class, YahooEmail[].class));
        primary(GENDER, new GenderConverter("m", "f"));
        primary(IMAGE, new JsonConverter<>(YahooImage.class));
        primary(INTERESTS, new JsonListConverter(YahooInterest.class, YahooInterest[].class));
        primary(CREATED, Converters.DATE_TZ_RFC822);
        primary(MEMBER_SINCE, Converters.DATE_TZ_RFC822);
        primary(UPDATED, Converters.DATE_TZ_RFC822);
    }
}
