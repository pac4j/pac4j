package org.pac4j.oauth.profile.strava;

import java.util.Date;
import java.util.List;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Strava with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.StravaClient}.</p>
 *
 * @since 1.7.0
 * @author Adrian Papusoi
 */
public class StravaProfile extends OAuth20Profile {

    private static final long serialVersionUID = 995023712830997358L;

    private static final String STRAVA_PROFILE_BASE_URL = "http://www.strava.com/athletes/";

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new StravaAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(StravaAttributesDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(StravaAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(StravaAttributesDefinition.FIRST_NAME) + " " + getAttribute(StravaAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getEmail() {
        return (String) getAttribute(StravaAttributesDefinition.EMAIL);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(StravaAttributesDefinition.PROFILE);
    }

    @Override
    public String getProfileUrl() {
        return STRAVA_PROFILE_BASE_URL + (String) getAttribute(StravaAttributesDefinition.ID);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(StravaAttributesDefinition.CITY);
    }

    @Override
    public Gender getGender() {
        return (Gender) getAttribute(StravaAttributesDefinition.SEX);
    }

    public Integer getResourceState() {
        return (Integer) getAttribute(StravaAttributesDefinition.RESOURCE_STATE);
    }

    public String getProfileMedium() {
        return (String) getAttribute(StravaAttributesDefinition.PROFILE_MEDIUM);
    }

    public String getState() {
        return (String) getAttribute(StravaAttributesDefinition.STATE);
    }

    public String getCountry() {
        return (String) getAttribute(StravaAttributesDefinition.COUNTRY);
    }

    public Boolean isPremium() {
        return (Boolean) getAttribute(StravaAttributesDefinition.PREMIUM);
    }

    public Date getCreatedAt() {
        return (Date) getAttribute(StravaAttributesDefinition.CREATED_AT);
    }

    public Date getUpdatedAt() {
        return (Date) getAttribute(StravaAttributesDefinition.UPDATED_AT);
    }

    public Integer getFollowerCount() {
        return (Integer) getAttribute(StravaAttributesDefinition.FOLLOWER_COUNT);
    }

    public Integer getFriendCount() {
        return (Integer) getAttribute(StravaAttributesDefinition.FRIEND_COUNT);
    }

    public String getDatePreference() {
        return (String) getAttribute(StravaAttributesDefinition.DATE_PREFERENCE);
    }

    public String getMeasurementPreference() {
        return (String) getAttribute(StravaAttributesDefinition.MEASUREMENT_PREFERENCE);
    }

    public List<StravaGear> getBikes() {
        return (List<StravaGear>) getAttribute(StravaAttributesDefinition.BIKES);
    }

    public List<StravaGear> getShoes() {
        return (List<StravaGear>) getAttribute(StravaAttributesDefinition.SHOES);
    }

    public List<StravaClub> getClubs() {
        return (List<StravaClub>) getAttribute(StravaAttributesDefinition.CLUBS);
    }

}
