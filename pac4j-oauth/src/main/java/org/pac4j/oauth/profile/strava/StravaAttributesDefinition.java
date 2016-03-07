package org.pac4j.oauth.profile.strava;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the Strava profile.
 *
 * @since 1.7.0
 * @author Adrian Papusoi
 */
public class StravaAttributesDefinition extends AttributesDefinition {

    public static final String ID = "id";
    public static final String RESOURCE_STATE = "resource_state";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String PROFILE_MEDIUM = "profile_medium";
    public static final String PROFILE = "profile";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String SEX = "sex";
    // friend
    // follower
    public static final String PREMIUM = "premium";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String BADGE_TYPE_ID = "badge_type_id";
    public static final String FOLLOWER_COUNT = "follower_count";
    public static final String FRIEND_COUNT = "friend_count";
    // mutual_friend_count
    public static final String DATE_PREFERENCE = "date_preference";
    public static final String MEASUREMENT_PREFERENCE = "measurement_preference";
    public static final String EMAIL = "email";
    // ftp
    public static final String CLUBS = "clubs";
    public static final String BIKES = "bikes";
    public static final String SHOES = "shoes";

    public StravaAttributesDefinition() {
        Arrays.stream(new String[] { FIRST_NAME, LAST_NAME, PROFILE_MEDIUM, PROFILE, CITY, STATE, COUNTRY,
                DATE_PREFERENCE, MEASUREMENT_PREFERENCE, EMAIL }).forEach(a -> primary(a, Converters.STRING));
        primary(ID, Converters.LONG);
        primary(RESOURCE_STATE, Converters.INTEGER);
        primary(BADGE_TYPE_ID, Converters.INTEGER);
        primary(FOLLOWER_COUNT, Converters.INTEGER);
        primary(FRIEND_COUNT, Converters.INTEGER);
        primary(PREMIUM, Converters.BOOLEAN);
        primary(SEX, new GenderConverter("m", "f"));
        primary(CREATED_AT, Converters.DATE_TZ_RFC822);
        primary(UPDATED_AT, Converters.DATE_TZ_RFC822);
        primary(CLUBS, new JsonListConverter(StravaClub.class, StravaClub[].class));
        final JsonListConverter multiGearConverter = new JsonListConverter(StravaGear.class, StravaGear[].class);
        primary(BIKES, multiGearConverter);
        primary(SHOES, multiGearConverter);
    }
}
