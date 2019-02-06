package org.pac4j.oauth.profile.google2;

import java.net.URI;
import java.util.Locale;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Google (using OAuth protocol version 2) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.Google2Client}.</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Profile extends OAuth20Profile {

    private static final long serialVersionUID = -7486869356444327783L;

    @Override
    public String getEmail() {
        return (String) getAttribute(Google2ProfileDefinition.EMAIL);
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(Google2ProfileDefinition.GIVEN_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(Google2ProfileDefinition.FAMILY_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(Google2ProfileDefinition.DISPLAY_NAME);
    }

    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(Google2ProfileDefinition.LANGUAGE);
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(Google2ProfileDefinition.PICTURE);
    }

    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(Google2ProfileDefinition.URL);
    }
}
