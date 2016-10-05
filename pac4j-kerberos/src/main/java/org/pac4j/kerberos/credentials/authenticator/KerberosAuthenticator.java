package org.pac4j.kerberos.credentials.authenticator;

import java.util.HashMap;

import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.http.credentials.authenticator.Authenticator;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for Kerberos. It creates the user profile and stores it in the credentials
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Garry Boyce
 * @since 1.8.10
 */
public class KerberosAuthenticator implements Authenticator<KerberosCredentials> {

    protected final Logger                logger = LoggerFactory.getLogger(getClass());

    private final KerberosTicketValidator ticketValidator;

    /**
     * Initializes the authenticator that will validate Kerberos tickets.
     *
     * @param ticketValidator    The ticket validator used to validate the Kerberos ticket.
     * @since 1.8.10
     */
    public KerberosAuthenticator(KerberosTicketValidator ticketValidator) {
        this.ticketValidator = ticketValidator;
    }

    @Override
    public void validate(KerberosCredentials credentials) {
        logger.debug("Try to validate Kerberos Token:" + credentials.getKerberosTicket());
        KerberosTicketValidation ticketValidation = this.ticketValidator.validateTicket(credentials.getKerberosTicket());
        logger.debug("Kerberos Token validated");

        String subject = ticketValidation.username();
        logger.debug("Succesfully validated " + subject);

        if (!subject.contains(UserProfile.SEPARATOR)) {
            subject = KerberosProfile.class.getSimpleName() + UserProfile.SEPARATOR + subject;
        }

        UserProfile profile = null;
        try {
            profile = ProfileHelper.buildUserProfileByClassCompleteName(subject, new HashMap<String, Object>(), KerberosProfile.class.getName());
        } catch (final Exception e) {
            logger.error("Cannot build instance", e);
        }

        credentials.setUserProfile(profile);
    }

}
