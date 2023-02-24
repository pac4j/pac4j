package org.pac4j.kerberos.credentials.authenticator;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Authenticator for Kerberos. It creates the user profile and stores it in the credentials
 * for the {@link org.pac4j.core.profile.creator.AuthenticatorProfileCreator}.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class KerberosAuthenticator implements Authenticator {

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

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        val credentials = (KerberosCredentials) cred;
        logger.trace("Try to validate Kerberos Token:" + credentials.getKerberosTicketAsString());
        var ticketValidation = this.ticketValidator.validateTicket(credentials.getKerberosTicket());
        logger.debug("Kerberos Token validated");

        var subject = ticketValidation.username();
        logger.debug("Succesfully validated " + subject);

        var profile = new KerberosProfile(ticketValidation.getGssContext());
        profile.setId(subject);
        credentials.setUserProfile(profile);
        return Optional.of(credentials);
    }
}
