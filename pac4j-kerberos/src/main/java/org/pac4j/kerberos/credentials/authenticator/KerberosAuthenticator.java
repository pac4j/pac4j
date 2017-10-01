package org.pac4j.kerberos.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for Kerberos. It creates the user profile and stores it in the credentials
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class KerberosAuthenticator implements Authenticator<KerberosCredentials> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected KerberosTicketValidator ticketValidator;

    /**
     * Initializes the authenticator that will validate Kerberos tickets.
     *
     * @param ticketValidator The ticket validator used to validate the Kerberos ticket.
     * @since 2.1.0
     */
    public KerberosAuthenticator(KerberosTicketValidator ticketValidator) {
        CommonHelper.assertNotNull("ticketValidator", ticketValidator);
        this.ticketValidator = ticketValidator;
    }

    @Override
    public void validate(KerberosCredentials credentials, WebContext context) {
        logger.trace("Try to validate Kerberos Token:" + credentials.getKerberosTicketAsString());
        KerberosTicketValidation ticketValidation = this.ticketValidator.validateTicket(credentials.getKerberosTicket());
        logger.debug("Kerberos Token validated");

        String subject = ticketValidation.username();
        logger.debug("Succesfully validated " + subject);

        KerberosProfile profile = new KerberosProfile();
        profile.setId(subject);
        profile.gssContext = ticketValidation.getGssContext();
        credentials.setUserProfile(profile);
    }
}
