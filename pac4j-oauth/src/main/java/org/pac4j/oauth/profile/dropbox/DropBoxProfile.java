package org.pac4j.oauth.profile.dropbox;

import java.net.URI;
import java.util.Locale;

import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for DropBox with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.DropBoxClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfile extends OAuth10Profile {

    private static final long serialVersionUID = 6671295443243112368L;

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(DropBoxProfileDefinition.COUNTRY);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(DropBoxProfileDefinition.REFERRAL_LINK);
    }

    /**
     * <p>getEmailVerified.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(DropBoxProfileDefinition.EMAIL_VERIFIED);
    }
}
