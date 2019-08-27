package org.pac4j.oauth.profile.linkedin2;

import java.net.URI;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileEmails.Email;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileEmails.Email.HandleTilde;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfilePicture.DisplayImageTilde;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfilePicture.DisplayImageTilde.Element;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfilePicture.DisplayImageTilde.Element.Identifier;

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
        final LinkedIn2ProfilePicture pp = getProfilePicture();
        if (pp == null)
            return null;

        final DisplayImageTilde displayImageTilde = pp.getDisplayImageTilde();
        if (displayImageTilde == null)
            return null;

        final Element[] elements = displayImageTilde.getElements();
        if (elements == null || elements.length == 0)
            return null;

        final Element element = elements[0];
        if (element == null)
            return null;

        final Identifier[] identifiers = element.getIdentifiers();
        if (identifiers == null || identifiers.length == 0)
            return null;

        final Identifier identifier = identifiers[0];
        if (identifier == null)
            return null;

        final String identifier2 = identifier.getIdentifier();
        if (identifier2 == null)
            return null;

        return CommonHelper.asURI(identifier2);
    }

    @Override
    public String getEmail() {
        final LinkedIn2ProfileEmails pe = getProfileEmails();

        if (pe == null)
            return null;

        final Email[] elements = pe.getElements();
        if (elements == null || elements.length == 0)
            return null;

        final Email element = elements[0];
        if (element == null)
            return null;

        final HandleTilde handleTilde = element.getHandleTilde();
        if (handleTilde == null)
            return null;

        return handleTilde.getEmailAddress();
    }
}
