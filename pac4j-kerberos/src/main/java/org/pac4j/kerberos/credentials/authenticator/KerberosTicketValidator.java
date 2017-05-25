package org.pac4j.kerberos.credentials.authenticator;

import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.kerberos.client.direct.KerberosClient;

/**
 * Implementations of this interface are used in
 * {@link KerberosClient} to validate a Kerberos/SPNEGO
 * Ticket.
 *
 * @author Garry Boyce
 * @see KerberosClient
 * <p>
 * originally from spring-kerberos project
 * @since 1.9.1
 */
public interface KerberosTicketValidator {

    /**
     * Validates a Kerberos/SPNEGO ticket.
     *
     * @param token Kerbeos/SPNEGO ticket
     * @return authenticated kerberos principal
     * @throws BadCredentialsException if the ticket is not valid
     */
    KerberosTicketValidation validateTicket(byte[] token) throws BadCredentialsException;
}
