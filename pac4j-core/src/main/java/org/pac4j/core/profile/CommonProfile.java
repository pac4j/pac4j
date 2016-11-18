package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.net.URI;
import java.util.Locale;

/**
 * This class is the base implementation with the default attribute getters which can be retrieved for most profiles.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CommonProfile extends UserProfile {
    
    private static final long serialVersionUID = -1856159870249261877L;

    /**
     * Return the email of the user.
     * 
     * @return the email of the user
     */
    public String getEmail() {
        final Object o = getAttribute(CommonProfileDefinition.EMAIL);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }
    
    /**
     * Return the first name of the user.
     * 
     * @return the first name of the user
     */
    public String getFirstName() {
        final Object o = getAttribute(CommonProfileDefinition.FIRST_NAME);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }
    
    /**
     * Return the family name of the user.
     * 
     * @return the family name of the user
     */
    public String getFamilyName() {
        final Object o = getAttribute(CommonProfileDefinition.FAMILY_NAME);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }
    
    /**
     * Return the displayed name of the user. It can be the username or the first and last names (separated by a space).
     * 
     * @return the displayed name of the user
     */
    public String getDisplayName() {
        final Object o = getAttribute(CommonProfileDefinition.DISPLAY_NAME);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }
    
    /**
     * Return the username of the user. It can be a login or a specific username.
     * 
     * @return the username of the user
     */
    public String getUsername() {
        final Object o = getAttribute(Pac4jConstants.USERNAME);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
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
        final Object o = getAttribute(CommonProfileDefinition.LOCALE);
        if (o instanceof Locale) {
            return (Locale) o;
        }
        return null;
    }
    
    /**
     * Return the url of the picture of the user.
     * 
     * @return the url of the picture of the user.
     */
    public URI getPictureUrl() {
        final Object o = getAttribute(CommonProfileDefinition.PICTURE_URL);
        if (o instanceof URI) {
            return (URI) o;
        }
        return null;
    }
    
    /**
     * Return the url of the profile of the user.
     * 
     * @return the url of the profile of the user.
     */
    public URI getProfileUrl() {
        final Object o = getAttribute(CommonProfileDefinition.PROFILE_URL);
        if (o instanceof URI) {
            return (URI) o;
        }
        return null;
    }
    
    /**
     * Return the location of the user.
     * 
     * @return the location of the user
     */
    public String getLocation() {
        final Object o = getAttribute(CommonProfileDefinition.LOCATION);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }
}
