package org.pac4j.oauth.profile.foursquare;

import java.io.Serializable;

/**
 * This class represents a Foursquare user photo.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserPhoto implements Serializable {

    private static final long serialVersionUID = -6808386671187616407L;

    private String prefix;
    private String suffix;

    /**
     * <p>getPhotoUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPhotoUrl() {
        return prefix + "original" + suffix;
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Setter for the field <code>prefix</code>.</p>
     *
     * @param prefix a {@link java.lang.String} object
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>suffix</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * <p>Setter for the field <code>suffix</code>.</p>
     *
     * @param suffix a {@link java.lang.String} object
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
