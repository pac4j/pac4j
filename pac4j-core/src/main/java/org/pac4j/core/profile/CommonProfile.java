package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.net.URI;
import java.security.Principal;
import java.util.Locale;

/**
 * This class is the base implementation with the default attribute getters which can be retrieved for most profiles.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CommonProfile extends BasicUserProfile {

    private static final long serialVersionUID = -1856159870249261877L;

    public CommonProfile() {
        this(true);
    }
    
    /**
     * Create a profile with possibility to merge attributes with the same name and collection-type values
     * @param canMergeAttributes if true - merge attributes with the same name and collection-type values, if false - overwrite them
     * @since 3.1.0
     */
    public CommonProfile(final boolean canMergeAttributes) {
        super(canMergeAttributes);
    }

    /**
     * Return the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return (String) getAttribute(CommonProfileDefinition.EMAIL);
    }

    /**
     * Return the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return (String) getAttribute(CommonProfileDefinition.FIRST_NAME);
    }

    /**
     * Return the family name of the user.
     *
     * @return the family name of the user
     */
    public String getFamilyName() {
        return (String) getAttribute(CommonProfileDefinition.FAMILY_NAME);
    }

    /**
     * Return the displayed name of the user. It can be the username or the first and last names (separated by a space).
     *
     * @return the displayed name of the user
     */
    public String getDisplayName() {
        return (String) getAttribute(CommonProfileDefinition.DISPLAY_NAME);
    }

    /**
     * Return the username of the user. It can be a login or a specific username.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return (String) getAttribute(Pac4jConstants.USERNAME);
    }

    /**
     * Return the gender of the user.
     *
     * @return the gender of the user
     */
    public Gender getGender() {
        final Gender gender = (Gender) getAttribute(CommonProfileDefinition.GENDER);
        if (gender == null) {
            return Gender.UNSPECIFIED;
        } else {
            return gender;
        }
    }

    /**
     * Return the locale of the user.
     *
     * @return the locale of the user
     */
    public Locale getLocale() {
        return (Locale) getAttribute(CommonProfileDefinition.LOCALE);
    }

    /**
     * Return the url of the picture of the user.
     *
     * @return the url of the picture of the user.
     */
    public URI getPictureUrl() {
        return (URI) getAttribute(CommonProfileDefinition.PICTURE_URL);
    }

    /**
     * Return the url of the profile of the user.
     *
     * @return the url of the profile of the user.
     */
    public URI getProfileUrl() {
        return (URI) getAttribute(CommonProfileDefinition.PROFILE_URL);
    }

    /**
     * Return the location of the user.
     *
     * @return the location of the user
     */
    public String getLocation() {
        return (String) getAttribute(CommonProfileDefinition.LOCATION);
    }

    public Principal asPrincipal() {
        return new Pac4JPrincipal(this);
    }

    public boolean isExpired() {
        return false;
    }
}
