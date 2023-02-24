package org.pac4j.oauth.profile.strava;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;
import java.util.List;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Strava profile definition.
 *
 * @since 1.7.0
 * @author Adrian Papusoi
 */
public class StravaProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>ID="id"</code> */
    public static final String ID = "id";
    /** Constant <code>RESOURCE_STATE="resource_state"</code> */
    public static final String RESOURCE_STATE = "resource_state";
    /** Constant <code>FIRST_NAME="firstname"</code> */
    public static final String FIRST_NAME = "firstname";
    /** Constant <code>LAST_NAME="lastname"</code> */
    public static final String LAST_NAME = "lastname";
    /** Constant <code>PROFILE_MEDIUM="profile_medium"</code> */
    public static final String PROFILE_MEDIUM = "profile_medium";
    /** Constant <code>PROFILE="profile"</code> */
    public static final String PROFILE = "profile";
    /** Constant <code>CITY="city"</code> */
    public static final String CITY = "city";
    /** Constant <code>STATE="state"</code> */
    public static final String STATE = "state";
    /** Constant <code>COUNTRY="country"</code> */
    public static final String COUNTRY = "country";
    /** Constant <code>SEX="sex"</code> */
    public static final String SEX = "sex";
    // friend
    // follower
    /** Constant <code>PREMIUM="premium"</code> */
    public static final String PREMIUM = "premium";
    /** Constant <code>CREATED_AT="created_at"</code> */
    public static final String CREATED_AT = "created_at";
    /** Constant <code>UPDATED_AT="updated_at"</code> */
    public static final String UPDATED_AT = "updated_at";
    /** Constant <code>BADGE_TYPE_ID="badge_type_id"</code> */
    public static final String BADGE_TYPE_ID = "badge_type_id";
    /** Constant <code>FOLLOWER_COUNT="follower_count"</code> */
    public static final String FOLLOWER_COUNT = "follower_count";
    /** Constant <code>FRIEND_COUNT="friend_count"</code> */
    public static final String FRIEND_COUNT = "friend_count";
    // mutual_friend_count
    /** Constant <code>DATE_PREFERENCE="date_preference"</code> */
    public static final String DATE_PREFERENCE = "date_preference";
    /** Constant <code>MEASUREMENT_PREFERENCE="measurement_preference"</code> */
    public static final String MEASUREMENT_PREFERENCE = "measurement_preference";
    // ftp
    /** Constant <code>CLUBS="clubs"</code> */
    public static final String CLUBS = "clubs";
    /** Constant <code>BIKES="bikes"</code> */
    public static final String BIKES = "bikes";
    /** Constant <code>SHOES="shoes"</code> */
    public static final String SHOES = "shoes";

    /**
     * <p>Constructor for StravaProfileDefinition.</p>
     */
    public StravaProfileDefinition() {
        super(x -> new StravaProfile());
        Arrays.stream(new String[] {FIRST_NAME, LAST_NAME, PROFILE_MEDIUM, CITY, STATE, COUNTRY,
                DATE_PREFERENCE, MEASUREMENT_PREFERENCE}).forEach(a -> primary(a, Converters.STRING));
        primary(ID, Converters.LONG);
        primary(RESOURCE_STATE, Converters.INTEGER);
        primary(BADGE_TYPE_ID, Converters.INTEGER);
        primary(FOLLOWER_COUNT, Converters.INTEGER);
        primary(FRIEND_COUNT, Converters.INTEGER);
        primary(PREMIUM, Converters.BOOLEAN);
        primary(SEX, Converters.GENDER);
        primary(CREATED_AT, Converters.DATE_TZ_RFC822);
        primary(UPDATED_AT, Converters.DATE_TZ_RFC822);
        primary(CLUBS, new JsonConverter(List.class, new TypeReference<List<StravaClub>>() {}));
        val multiGearConverter = new JsonConverter(List.class, new TypeReference<List<StravaGear>>() {});
        primary(BIKES, multiGearConverter);
        primary(SHOES, multiGearConverter);
        primary(PROFILE, Converters.URL);
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://www.strava.com/api/v3/athlete";
    }

    /** {@inheritDoc} */
    @Override
    public StravaProfile extractUserProfile(String body) {
        val profile = (StravaProfile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, StravaProfileDefinition.ID)));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
