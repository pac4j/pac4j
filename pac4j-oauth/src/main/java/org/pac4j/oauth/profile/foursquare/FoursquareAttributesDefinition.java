package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

public class FoursquareAttributesDefinition extends OAuthAttributesDefinition {

    public static final String ID = "id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "family_name";
    public static final String GENDER = "gender";
    public static final String PHOTO = "picture_url";
    public static final String EMAIL = "email";
    public static final String FIRENDS = "friends";
    public static final String HOME_CITY = "homeCity";
    public static final String BIO = "bio";
    public static final String CONTACT = "contact";
    public static final String BADGES = "badges";

    public FoursquareAttributesDefinition() {
        String[] names = new String[] {
                FIRST_NAME,LAST_NAME,GENDER, HOME_CITY, BIO, EMAIL, PHOTO
        };
        for (final String name : names) {
            addAttribute(name, Converters.stringConverter);
        }

        addAttribute(GENDER, Converters.genderConverter);
        addAttribute(FIRENDS,FoursquareConverters.friendsConverter);
    }

}
