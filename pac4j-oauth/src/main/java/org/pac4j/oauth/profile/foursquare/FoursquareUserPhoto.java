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

    public String getPhotoUrl() {
        return prefix + "original" + suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
}
