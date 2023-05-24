package org.pac4j.core.profile;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.Pac4jConstants;

import java.io.Serial;
import java.net.URI;
import java.util.Date;
import java.util.Locale;

/**
 * This class is the base implementation with the default attribute getters which can be retrieved for most profiles.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
@ToString(callSuper = true)
public class CommonProfile extends BasicUserProfile {

    @Serial
    private static final long serialVersionUID = -1856159870249261877L;

    /**
     * <p>Constructor for CommonProfile.</p>
     */
    public CommonProfile() {
        this(true);
    }

    /**
     * Create a profile with possibility to merge attributes with the same name and collection-type values
     *
     * @param canMergeAttributes if true - merge attributes with the same name and collection-type values, if false -
     * overwrite them
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
        return getAttributeAsString(CommonProfileDefinition.EMAIL);
    }

    /**
     * Return the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return getAttributeAsString(CommonProfileDefinition.FIRST_NAME);
    }

    /**
     * Return the family name of the user.
     *
     * @return the family name of the user
     */
    public String getFamilyName() {
        return getAttributeAsString(CommonProfileDefinition.FAMILY_NAME);
    }

    /**
     * Return the displayed name of the user. It can be the username or the first and last names (separated by a space).
     *
     * @return the displayed name of the user
     */
    public String getDisplayName() {
        return getAttributeAsString(CommonProfileDefinition.DISPLAY_NAME);
    }

    /**
     * {@inheritDoc}
     *
     * Return the username of the user. It can be a login or a specific username.
     */
    @Override
    public String getUsername() {
        return getAttributeAsString(Pac4jConstants.USERNAME);
    }

    /**
     * Return the gender of the user.
     *
     * @return the gender of the user
     */
    public Gender getGender() {
        return getAttributeAsType(CommonProfileDefinition.GENDER, Gender.class, Gender.UNSPECIFIED);
    }

    /**
     * Return the locale of the user.
     *
     * @return the locale of the user
     */
    public Locale getLocale() {
        return getAttributeAsType(CommonProfileDefinition.LOCALE, Locale.class, null);
    }

    /**
     * Return the url of the picture of the user.
     *
     * @return the url of the picture of the user.
     */
    public URI getPictureUrl() {
        return getAttributeAsType(CommonProfileDefinition.PICTURE_URL, URI.class, null);
    }

    /**
     * Return the url of the profile of the user.
     *
     * @return the url of the profile of the user.
     */
    public URI getProfileUrl() {
        return getAttributeAsType(CommonProfileDefinition.PROFILE_URL, URI.class, null);
    }

    /**
     * Return the location of the user.
     *
     * @return the location of the user
     */
    public String getLocation() {
        return getAttributeAsString(CommonProfileDefinition.LOCATION);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExpired() {
        return false;
    }

    /**
     * <p>getAttributeAsString.</p>
     *
     * @param name a {@link String} object
     * @return a {@link String} object
     */
    protected String getAttributeAsString(final String name) {
        val value = getAttribute(name);
        if (value != null) {
            return value.toString();
        }
        else {
            return null;
        }
    }

    /**
     * <p>getAttributeAsType.</p>
     *
     * @param name a {@link String} object
     * @param clazz a {@link Class} object
     * @param defaultValue a T object
     * @param <T> a T class
     * @return a T object
     */
    protected <T> T getAttributeAsType(final String name, Class<T> clazz, T defaultValue) {
        val value = getAttribute(name);
        if (value != null && clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        }
        else {
            return defaultValue;
        }
    }

    /**
     * <p>getAttributeAsDate.</p>
     *
     * @param name a {@link String} object
     * @return a {@link Date} object
     */
    protected Date getAttributeAsDate(final String name) {
        val value = getAttribute(name);
        // it should be a Date, but in case it's a Long (Vertx issue with profiles serialized to JSON and restored)
        if (value instanceof Long l) {
            return new Date(l);
        }
        else if (value instanceof Double d) {
            return new Date(d.longValue());
        }
        else {
            return (Date) getAttribute(name);
        }
    }
}
