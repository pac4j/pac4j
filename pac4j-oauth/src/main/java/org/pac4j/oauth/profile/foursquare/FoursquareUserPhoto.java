package org.pac4j.oauth.profile.foursquare;

import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a Foursquare user photo.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserPhoto extends JsonObject {

    private static final long serialVersionUID = -6808386671187616407L;

    private String prefix;
    private String suffix;

    public String getPhotoUrl() {
        return prefix + "original" + suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
