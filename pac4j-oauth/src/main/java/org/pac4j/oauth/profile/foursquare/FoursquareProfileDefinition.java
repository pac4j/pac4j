package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.Arrays;

/**
 * This class is the Foursquare profile definition.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareProfileDefinition extends CommonProfileDefinition<FoursquareProfile> {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHOTO = "photo";
    public static final String FIRENDS = "friends";
    public static final String HOME_CITY = "homeCity";
    public static final String CONTACT = "contact";
    public static final String BIO = "bio";

    public FoursquareProfileDefinition() {
        super(x -> new FoursquareProfile());
        Arrays.stream(new String[] {
                FIRST_NAME, LAST_NAME, HOME_CITY, BIO, PHOTO
        }).forEach(a -> primary(a, Converters.STRING));
        primary(GENDER, Converters.GENDER);
        primary(FIRENDS, new JsonConverter<>(FoursquareUserFriends.class));
        primary(CONTACT, new JsonConverter<>(FoursquareUserContact.class));
        primary(PHOTO, new JsonConverter<>(FoursquareUserPhoto.class));
    }
}
