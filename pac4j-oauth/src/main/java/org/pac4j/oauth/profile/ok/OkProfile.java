package org.pac4j.oauth.profile.ok;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

import java.util.Locale;

/**
 * Represents basic (OAuth20Profile) profile on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkProfile extends OAuth20Profile {

    private static final long serialVersionUID = -810631113167677397L;

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.okDefinition;
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(OkAttributesDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(OkAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(OkAttributesDefinition.NAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(OkAttributesDefinition.UID);
    }

    @Override
    public Gender getGender() {
        return Gender.valueOf(((String) getAttribute(OkAttributesDefinition.GENDER)).toUpperCase());
    }

    @Override
    public Locale getLocale() {
        return new Locale((String) getAttribute(OkAttributesDefinition.LOCALE));
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(OkAttributesDefinition.PIC_1);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(OkAttributesDefinition.PIC_2);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(OkAttributesDefinition.LOCATION_CITY) +
                ", " +
                (String) getAttribute(OkAttributesDefinition.LOCATION_COUNTRY);
    }
}
