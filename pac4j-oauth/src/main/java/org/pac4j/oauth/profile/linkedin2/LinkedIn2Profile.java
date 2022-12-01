package org.pac4j.oauth.profile.linkedin2;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.net.URI;

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
        val pp = getProfilePicture();
        if (pp == null) {
            return null;
        }

        val displayImageTilde = pp.getDisplayImageTilde();
        if (displayImageTilde == null) {
            return null;
        }

        val elements = displayImageTilde.getElements();
        if (elements == null || elements.length == 0) {
            return null;
        }

        val element = elements[0];
        if (element == null) {
            return null;
        }

        val identifiers = element.getIdentifiers();
        if (identifiers == null || identifiers.length == 0) {
            return null;
        }

        val identifier = identifiers[0];
        if (identifier == null) {
            return null;
        }

        val identifier2 = identifier.getIdentifier();
        if (identifier2 == null) {
            return null;
        }

        return CommonHelper.asURI(identifier2);
    }

    @Override
    public String getEmail() {
        val pe = getProfileEmails();

        if (pe == null) {
            return null;
        }

        val elements = pe.getElements();
        if (elements == null || elements.length == 0) {
            return null;
        }

        val element = elements[0];
        if (element == null) {
            return null;
        }

        val handleTilde = element.getHandleTilde();
        if (handleTilde == null) {
            return null;
        }

        return handleTilde.getEmailAddress();
    }
}
