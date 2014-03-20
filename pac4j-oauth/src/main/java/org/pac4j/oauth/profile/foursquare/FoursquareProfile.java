package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

public class FoursquareProfile extends OAuth20Profile {
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.foursquareDefenition;
    }

    public static final String PHOTO = "photo";
    public static final String FIRENDS = "friends";
    public static final String HOME_CITY = "homeCity";
    public static final String BIO = "bio";

    public String getBio() {
        return (String) getAttribute(FoursquareAttributesDefinition.BIO);
    }

    public String getHomeCity() {
        return (String) getAttribute(FoursquareAttributesDefinition.HOME_CITY);
    }
}
