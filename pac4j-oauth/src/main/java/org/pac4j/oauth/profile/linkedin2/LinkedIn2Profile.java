package org.pac4j.oauth.profile.linkedin2;

import java.net.URI;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 *
 * @author Vassilis Virvilis
 */
public class LinkedIn2Profile extends OAuth20Profile {
    private static final long serialVersionUID = 100L;

    public String getLocalizedFirstName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.LOCALIZED_FIRST_NAME);
    }

    public String getLocalizedLastName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.LOCALIZED_LAST_NAME);
    }

    public LinkedIn2ProfilePicture getProfilePicture() {
        return (LinkedIn2ProfilePicture) getAttribute(LinkedIn2ProfileDefinition.PROFILE_PICTURE);
    }

    public LinkedIn2ProfileEmails getProfileEmails() {
        return (LinkedIn2ProfileEmails) getAttribute(LinkedIn2ProfileDefinition.PROFILE_EMAILS);
    }

    @Override
    public String getFirstName() {
        return getLocalizedFirstName();
    }

    @Override
    public String getFamilyName() {
        return getLocalizedLastName();
    }

    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }

    @Override
    public URI getPictureUrl() {
        final var pp = getProfilePicture();
        if (pp == null) {
            return null;
        }

        final var displayImageTilde = pp.getDisplayImageTilde();
        if (displayImageTilde == null) {
            return null;
        }

        final var elements = displayImageTilde.getElements();
        if (elements == null || elements.length == 0) {
            return null;
        }

        final var element = elements[0];
        if (element == null) {
            return null;
        }

        final var identifiers = element.getIdentifiers();
        if (identifiers == null || identifiers.length == 0) {
            return null;
        }

        final var identifier = identifiers[0];
        if (identifier == null) {
            return null;
        }

        final var identifier2 = identifier.getIdentifier();
        if (identifier2 == null) {
            return null;
        }

        return CommonHelper.asURI(identifier2);
    }

    @Override
    public String getEmail() {
        final var pe = getProfileEmails();

        if (pe == null) {
            return null;
        }

        final var elements = pe.getElements();
        if (elements == null || elements.length == 0) {
            return null;
        }

        final var element = elements[0];
        if (element == null) {
            return null;
        }

        final var handleTilde = element.getHandleTilde();
        if (handleTilde == null) {
            return null;
        }

        return handleTilde.getEmailAddress();
    }
}
