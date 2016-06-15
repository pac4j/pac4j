package org.pac4j.kerberos.credentials.authenticator;

import java.util.HashMap;

import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for Kerberos. It creates the user profile and stores it in the credentials
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Garry Boyce
 * @since 1.9.1
 */
public class KerberosAuthenticator extends InitializableWebObject implements Authenticator<KerberosCredentials> {

    protected final Logger                logger = LoggerFactory.getLogger(getClass());

    private KerberosTicketValidator ticketValidator;

    public KerberosAuthenticator() {
	}

	/**
     * Initializes the authenticator that will validate Kerberos tickets.
     *
     * @param ticketValidator    The ticket validator used to validate the Kerberos ticket.
     * @since 1.9.1
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

        CommonProfile profile = null;
        try {
            profile = ProfileHelper.buildUserProfileByClassCompleteName(subject, new HashMap<String, Object>(), KerberosProfile.class.getName());
        } catch (final Exception e) {
            logger.error("Cannot build instance", e);
        }

        credentials.setUserProfile(profile);
    }

	@Override
	protected void internalInit(WebContext context) {
		CommonHelper.assertNotNull("ticketValidator", this.ticketValidator);
        if (this.ticketValidator instanceof InitializableObject) {
            ((InitializableObject) this.ticketValidator).init();
        }
	}

	public KerberosTicketValidator getTicketValidator() {
		return ticketValidator;
	}

	public void setTicketValidator(KerberosTicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}

}
