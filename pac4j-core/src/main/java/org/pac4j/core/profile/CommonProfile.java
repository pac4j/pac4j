package org.pac4j.core.profile;

import java.net.URI;
import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.Pac4jConstants;

/**
 * This class is the base implementation with the default attribute getters which can be retrieved for most profiles.
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
     * @param canMergeAttributes if true - merge attributes with the same name and collection-type values, if false -
     * overwrite them
     * @since 3.1.0
     */
    public CommonProfile(final boolean canMergeAttributes) {
        super(canMergeAttributes);
    }

    /**
     * Return the email of the user.
     * @return the email of the user
     */
    public String getEmail() {
        return getAttributeAsString(CommonProfileDefinition.EMAIL);
    }

    /**
     * Return the first name of the user.
     * @return the first name of the user
     */
    public String getFirstName() {
        return getAttributeAsString(CommonProfileDefinition.FIRST_NAME);
    }

    /**
     * Return the family name of the user.
     * @return the family name of the user
     */
    public String getFamilyName() {
        return getAttributeAsString(CommonProfileDefinition.FAMILY_NAME);
    }

    /**
     * Return the displayed name of the user. It can be the username or the first and last names (separated by a space).
     * @return the displayed name of the user
     */
    public String getDisplayName() {
        return getAttributeAsString(CommonProfileDefinition.DISPLAY_NAME);
    }

    /**
     * Return the username of the user. It can be a login or a specific username.
     * @return the username of the user
     */
    @Override
    public String getUsername() {
        return getAttributeAsString(Pac4jConstants.USERNAME);
    }

    /**
     * Return the gender of the user.
     * @return the gender of the user
     */
    public Gender getGender() {
        return getAttributeAsType(CommonProfileDefinition.GENDER, Gender.class, Gender.UNSPECIFIED);
    }

    /**
     * Return the locale of the user.
     * @return the locale of the user
     */
    public Locale getLocale() {
        return getAttributeAsType(CommonProfileDefinition.LOCALE, Locale.class, null);
    }

    /**
     * Return the url of the picture of the user.
     * @return the url of the picture of the user.
     */
    public URI getPictureUrl() {
        return getAttributeAsType(CommonProfileDefinition.PICTURE_URL, URI.class, null);
    }

    /**
     * Return the url of the profile of the user.
     * @return the url of the profile of the user.
     */
    public URI getProfileUrl() {
        return getAttributeAsType(CommonProfileDefinition.PROFILE_URL, URI.class, null);
    }

    /**
     * Return the location of the user.
     * @return the location of the user
     */
    public String getLocation() {
        return getAttributeAsString(CommonProfileDefinition.LOCATION);
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    protected String getAttributeAsString(final String name) {
        final var value = getAttribute(name);
        if (value != null) {
            return value.toString();
        }
        else {
            return null;
        }
    }

    protected <T> T getAttributeAsType(final String name, Class<T> clazz, T defaultValue) {
        final var value = getAttribute(name);
        if (value != null && clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        }
        else {
            return defaultValue;
        }
    }

    protected Date getAttributeAsDate(final String name) {
        final var value = getAttribute(name);
        // it should be a Date, but in case it's a Long (Vertx issue with profiles serialized to JSON and restored)
        if (value instanceof Long) {
            return new Date((long)value);
        }
        else if (value instanceof Double) {
            return new Date(((Double)value).longValue());
        }
        else {
            return (Date)getAttribute(name);
        }
    }
}
