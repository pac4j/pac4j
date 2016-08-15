package org.pac4j.oauth.profile.generic;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.util.Locale;

/**
 * <p>This is the user profile for the generic OAuth 2.0 client.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.GenericOAuth20Client}  </p>
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericOAuth20Profile extends OAuth20Profile {

    private transient GenericAttributesDefinition attributes = new GenericAttributesDefinition();

    @Override
    public GenericAttributesDefinition getAttributesDefinition() {
        return attributes;
    }

    /**
     * Used to set a custom AttributesDefinition.
     * Custom definition must extend {@link org.pac4j.oauth.profile.generic.GenericAttributesDefinition}
     *
     * @param attributes
     */
    public void setAttributesDefinition(GenericAttributesDefinition attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getEmail() {
        return (String) getAttribute(attributes.EMAIL);
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(attributes.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(attributes.FAMILY_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(attributes.DISPLAY_NAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(Pac4jConstants.USERNAME);
    }

    @Override
    public Gender getGender() {
        final Gender gender = (Gender) getAttribute(attributes.GENDER);
        if (gender == null) {
            return Gender.UNSPECIFIED;
        } else {
            return gender;
        }
    }

    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(attributes.GENDER);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(attributes.PICTURE_URL);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(attributes.PROFILE_URL);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(attributes.LOCATION);
    }
}