package org.pac4j.oauth.profile.strava;

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 995023712830997358L;

    private static final String STRAVA_PROFILE_BASE_URL = "http://www.strava.com/athletes/";

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(StravaProfileDefinition.FIRST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return (String) getAttribute(StravaProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return getAttribute(StravaProfileDefinition.FIRST_NAME) + " " + getAttribute(StravaProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(StravaProfileDefinition.PROFILE);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI(STRAVA_PROFILE_BASE_URL + getAttribute(StravaProfileDefinition.ID));
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        return (String) getAttribute(StravaProfileDefinition.CITY);
    }

    /** {@inheritDoc} */
    @Override
    public Gender getGender() {
        return (Gender) getAttribute(StravaProfileDefinition.SEX);
    }

    /**
     * <p>getResourceState.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getResourceState() {
        return (Integer) getAttribute(StravaProfileDefinition.RESOURCE_STATE);
    }

    /**
     * <p>getProfileMedium.</p>
     *
     * @return a {@link String} object
     */
    public String getProfileMedium() {
        return (String) getAttribute(StravaProfileDefinition.PROFILE_MEDIUM);
    }

    /**
     * <p>getState.</p>
     *
     * @return a {@link String} object
     */
    public String getState() {
        return (String) getAttribute(StravaProfileDefinition.STATE);
    }

    /**
     * <p>getCountry.</p>
     *
     * @return a {@link String} object
     */
    public String getCountry() {
        return (String) getAttribute(StravaProfileDefinition.COUNTRY);
    }

    /**
     * <p>isPremium.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isPremium() {
        return (Boolean) getAttribute(StravaProfileDefinition.PREMIUM);
    }

    /**
     * <p>getCreatedAt.</p>
     *
     * @return a {@link Date} object
     */
    public Date getCreatedAt() {
        return (Date) getAttribute(StravaProfileDefinition.CREATED_AT);
    }

    /**
     * <p>getUpdatedAt.</p>
     *
     * @return a {@link Date} object
     */
    public Date getUpdatedAt() {
        return (Date) getAttribute(StravaProfileDefinition.UPDATED_AT);
    }

    /**
     * <p>getFollowerCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFollowerCount() {
        return (Integer) getAttribute(StravaProfileDefinition.FOLLOWER_COUNT);
    }

    /**
     * <p>getFriendCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFriendCount() {
        return (Integer) getAttribute(StravaProfileDefinition.FRIEND_COUNT);
    }

    /**
     * <p>getDatePreference.</p>
     *
     * @return a {@link String} object
     */
    public String getDatePreference() {
        return (String) getAttribute(StravaProfileDefinition.DATE_PREFERENCE);
    }

    /**
     * <p>getMeasurementPreference.</p>
     *
     * @return a {@link String} object
     */
    public String getMeasurementPreference() {
        return (String) getAttribute(StravaProfileDefinition.MEASUREMENT_PREFERENCE);
    }

    /**
     * <p>getBikes.</p>
     *
     * @return a {@link List} object
     */
    public List<StravaGear> getBikes() {
        return (List<StravaGear>) getAttribute(StravaProfileDefinition.BIKES);
    }

    /**
     * <p>getShoes.</p>
     *
     * @return a {@link List} object
     */
    public List<StravaGear> getShoes() {
        return (List<StravaGear>) getAttribute(StravaProfileDefinition.SHOES);
    }

    /**
     * <p>getClubs.</p>
     *
     * @return a {@link List} object
     */
    public List<StravaClub> getClubs() {
        return (List<StravaClub>) getAttribute(StravaProfileDefinition.CLUBS);
    }

}
