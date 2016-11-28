package org.pac4j.oauth.profile.strava;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.pac4j.core.profile.Gender;
import org.pac4j.core.util.CommonHelper;
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

    @Override
    public String getFirstName() {
        return (String) getAttribute(StravaProfileDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(StravaProfileDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(StravaProfileDefinition.FIRST_NAME) + " " + getAttribute(StravaProfileDefinition.LAST_NAME);
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(StravaProfileDefinition.PROFILE);
    }

    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI(STRAVA_PROFILE_BASE_URL + (String) getAttribute(StravaProfileDefinition.ID));
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(StravaProfileDefinition.CITY);
    }

    @Override
    public Gender getGender() {
        return (Gender) getAttribute(StravaProfileDefinition.SEX);
    }

    public Integer getResourceState() {
        return (Integer) getAttribute(StravaProfileDefinition.RESOURCE_STATE);
    }

    public String getProfileMedium() {
        return (String) getAttribute(StravaProfileDefinition.PROFILE_MEDIUM);
    }

    public String getState() {
        return (String) getAttribute(StravaProfileDefinition.STATE);
    }

    public String getCountry() {
        return (String) getAttribute(StravaProfileDefinition.COUNTRY);
    }

    public Boolean isPremium() {
        return (Boolean) getAttribute(StravaProfileDefinition.PREMIUM);
    }

    public Date getCreatedAt() {
        return (Date) getAttribute(StravaProfileDefinition.CREATED_AT);
    }

    public Date getUpdatedAt() {
        return (Date) getAttribute(StravaProfileDefinition.UPDATED_AT);
    }

    public Integer getFollowerCount() {
        return (Integer) getAttribute(StravaProfileDefinition.FOLLOWER_COUNT);
    }

    public Integer getFriendCount() {
        return (Integer) getAttribute(StravaProfileDefinition.FRIEND_COUNT);
    }

    public String getDatePreference() {
        return (String) getAttribute(StravaProfileDefinition.DATE_PREFERENCE);
    }

    public String getMeasurementPreference() {
        return (String) getAttribute(StravaProfileDefinition.MEASUREMENT_PREFERENCE);
    }

    public List<StravaGear> getBikes() {
        return (List<StravaGear>) getAttribute(StravaProfileDefinition.BIKES);
    }

    public List<StravaGear> getShoes() {
        return (List<StravaGear>) getAttribute(StravaProfileDefinition.SHOES);
    }

    public List<StravaClub> getClubs() {
        return (List<StravaClub>) getAttribute(StravaProfileDefinition.CLUBS);
    }

}
