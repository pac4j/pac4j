package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;

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
        return (String) getAttribute("email");
    }
    
    /**
     * Return the first name of the user.
     * 
     * @return the first name of the user
     */
    public String getFirstName() {
        return (String) getAttribute("first_name");
    }
    
    /**
     * Return the family name of the user.
     * 
     * @return the family name of the user
     */
    public String getFamilyName() {
        return (String) getAttribute("family_name");
    }
    
    /**
     * Return the displayed name of the user. It can be the username or the first and last names (separated by a space).
     * 
     * @return the displayed name of the user
     */
    public String getDisplayName() {
        return (String) getAttribute("display_name");
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
        final Gender gender = (Gender) getAttribute("gender");
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
        return (Locale) getAttribute("locale");
    }
    
    /**
     * Return the url of the picture of the user.
     * 
     * @return the url of the picture of the user.
     */
    public String getPictureUrl() {
        return (String) getAttribute("picture_url");
    }
    
    /**
     * Return the url of the profile of the user.
     * 
     * @return the url of the profile of the user.
     */
    public String getProfileUrl() {
        return (String) getAttribute("profile_url");
    }
    
    /**
     * Return the location of the user.
     * 
     * @return the locationle of the user
     */
    public String getLocation() {
        return (String) getAttribute("location");
    }
}
