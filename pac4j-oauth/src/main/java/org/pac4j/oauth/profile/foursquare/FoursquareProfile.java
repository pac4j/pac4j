package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;
import java.net.URI;

/**
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = 8919122885219420820L;

    /**
     * <p>getBio.</p>
     *
     * @return a {@link String} object
     */
    public String getBio() {
        return (String) getAttribute(FoursquareProfileDefinition.BIO);
    }

    /**
     * <p>getContact.</p>
     *
     * @return a {@link FoursquareUserContact} object
     */
    public FoursquareUserContact getContact() {
        return (FoursquareUserContact) getAttribute(FoursquareProfileDefinition.CONTACT);
    }

    /**
     * <p>getFriends.</p>
     *
     * @return a {@link FoursquareUserFriends} object
     */
    public FoursquareUserFriends getFriends() {
        return (FoursquareUserFriends) getAttribute(FoursquareProfileDefinition.FIRENDS);
    }

    /**
     * <p>getPhoto.</p>
     *
     * @return a {@link FoursquareUserPhoto} object
     */
    public FoursquareUserPhoto getPhoto() {
        return (FoursquareUserPhoto) getAttribute(FoursquareProfileDefinition.PHOTO);
    }

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(FoursquareProfileDefinition.FIRST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        return (String) getAttribute(FoursquareProfileDefinition.HOME_CITY);
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return (String) getAttribute(FoursquareProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI("https://foursquare.com/user/" + getId());
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return CommonHelper.asURI(this.getPhoto().getPhotoUrl());
    }

    /** {@inheritDoc} */
    @Override
    public String getEmail() {
        return this.getContact().getEmail();
    }
}
