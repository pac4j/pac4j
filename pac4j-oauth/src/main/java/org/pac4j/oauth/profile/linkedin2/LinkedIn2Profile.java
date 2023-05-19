package org.pac4j.oauth.profile.linkedin2;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;
import java.net.URI;

/**
 * <p>LinkedIn2Profile class.</p>
 *
 * @author Vassilis Virvilis
 */
public class LinkedIn2Profile extends OAuth20Profile {
    @Serial
    private static final long serialVersionUID = 100L;

    /**
     * <p>getLocalizedFirstName.</p>
     *
     * @return a {@link String} object
     */
    public String getLocalizedFirstName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.LOCALIZED_FIRST_NAME);
    }

    /**
     * <p>getLocalizedLastName.</p>
     *
     * @return a {@link String} object
     */
    public String getLocalizedLastName() {
        return (String) getAttribute(LinkedIn2ProfileDefinition.LOCALIZED_LAST_NAME);
    }

    /**
     * <p>getProfilePicture.</p>
     *
     * @return a {@link LinkedIn2ProfilePicture} object
     */
    public LinkedIn2ProfilePicture getProfilePicture() {
        return (LinkedIn2ProfilePicture) getAttribute(LinkedIn2ProfileDefinition.PROFILE_PICTURE);
    }

    /**
     * <p>getProfileEmails.</p>
     *
     * @return a {@link LinkedIn2ProfileEmails} object
     */
    public LinkedIn2ProfileEmails getProfileEmails() {
        return (LinkedIn2ProfileEmails) getAttribute(LinkedIn2ProfileDefinition.PROFILE_EMAILS);
    }

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return getLocalizedFirstName();
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return getLocalizedLastName();
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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
