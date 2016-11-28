package org.pac4j.oauth.profile.ok;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;

/**
 * Represents basic (OAuth20Profile) profile on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkProfile extends OAuth20Profile {

    private static final long serialVersionUID = -810631113167677397L;

    public static final String BASE_PROFILE_URL = "http://ok.ru/profile/";

    @Override
    public String getFirstName() {
        return (String) getAttribute(OkProfileDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(OkProfileDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(OkProfileDefinition.NAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(OkProfileDefinition.UID);
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(OkProfileDefinition.PIC_1);
    }

    @Override
    public URI getProfileUrl() {
        return CommonHelper.asURI(BASE_PROFILE_URL + getId());
    }

    @Override
    public String getLocation() {
        return getAttribute(OkProfileDefinition.LOCATION_CITY) +
                ", " +
                getAttribute(OkProfileDefinition.LOCATION_COUNTRY);
    }
}
