package org.scribe.up.profile.facebook;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.GenderConverter;

public class FacebookProfileDefinition extends AttributesDefinition {
    
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";
    public static final String LANGUAGES = "languages";
    public static final String LINK = "link";
    public static final String USERNAME = "username";
    public static final String THIRD_PARTY_ID = "third_party_id";
    public static final String TIMEZONE = "timezone";
    public static final String UPDATED_TIME = "updated_time";
    public static final String VERIFIED = "verified";
    public static final String BIO = "bio";
    public static final String BIRTHDAY = "birthday";
    public static final String EDUCATION = "education";
    public static final String EMAIL = "email";
    public static final String HOMETOWN = "hometown";
    public static final String INTERESTED_IN = "interested_in";
    public static final String LOCATION = "location";
    public static final String POLITICAL = "political";
    public static final String FAVORITE_ATHLETES = "favorite_athletes";
    public static final String FAVORITE_TEAMS = "favorite_teams";
    public static final String QUOTES = "quotes";
    public static final String RELATIONSHIP_STATUS = "relationship_status";
    public static final String RELIGION = "religion";
    public static final String SIGNIFICANT_OTHER = "significant_other";
    public static final String WEBSITE = "website";
    public static final String WORK = "work";
    
    public FacebookProfileDefinition() {
        String[] names = new String[] {
            FacebookProfile.NAME, FacebookProfile.FIRST_NAME, FacebookProfile.MIDDLE_NAME, FacebookProfile.LAST_NAME,
            FacebookProfile.LINK, FacebookProfile.USERNAME, FacebookProfile.THIRD_PARTY_ID, FacebookProfile.BIO,
            FacebookProfile.EMAIL, FacebookProfile.POLITICAL, FacebookProfile.QUOTES, FacebookProfile.RELIGION,
            FacebookProfile.WEBSITE
        };
        for (String name : names) {
            attributes.add(name);
            converters.put(name, stringConverter);
        }
        names = new String[] {
            FacebookProfile.TIMEZONE
        };
        for (String name : names) {
            attributes.add(name);
            converters.put(name, safeIntegerConverter);
        }
        names = new String[] {
            FacebookProfile.VERIFIED
        };
        for (String name : names) {
            attributes.add(name);
            converters.put(name, safeBooleanConverter);
        }
        attributes.add(GENDER);
        converters.put(GENDER, new GenderConverter("male", "female"));
        attributes.add(LOCALE);
        converters.put(LOCALE, localeConverter);
        attributes.add(UPDATED_TIME);
        converters.put(UPDATED_TIME, new DateConverter("yyyy-MM-dd'T'HH:mm:ssz"));
        attributes.add(BIRTHDAY);
        converters.put(BIRTHDAY, new DateConverter("MM/dd/yyyy"));
        attributes.add(RELATIONSHIP_STATUS);
        converters.put(RELATIONSHIP_STATUS, new FacebookRelationshipStatusConverter());
    }
}
