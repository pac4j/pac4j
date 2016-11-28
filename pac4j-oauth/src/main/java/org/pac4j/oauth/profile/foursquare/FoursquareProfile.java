package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;

/**
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = 8919122885219420820L;

    public String getBio() {
        return (String) getAttribute(FoursquareProfileDefinition.BIO);
    }

    public FoursquareUserContact getContact() {
        return (FoursquareUserContact) getAttribute(FoursquareProfileDefinition.CONTACT);
    }

    public FoursquareUserFriends getFriends() {
        return (FoursquareUserFriends) getAttribute(FoursquareProfileDefinition.FIRENDS);
    }

    public FoursquareUserPhoto getPhoto() {
        return (FoursquareUserPhoto) getAttribute(FoursquareProfileDefinition.PHOTO);
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(FoursquareProfileDefinition.FIRST_NAME);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(FoursquareProfileDefinition.HOME_CITY);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(FoursquareProfileDefinition.LAST_NAME);
    }

    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI("https://foursquare.com/user/" + getId());
    }

    @Override
    public URI getPictureUrl() {
        return CommonHelper.asURI(this.getPhoto().getPhotoUrl());
    }

    @Override
    public String getEmail() {
        return this.getContact().getEmail();
    }
}
