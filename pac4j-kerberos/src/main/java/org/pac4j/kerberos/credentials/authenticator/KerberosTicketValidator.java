package org.pac4j.kerberos.credentials.authenticator;

import org.pac4j.core.exception.BadCredentialsException;

/**
 * Implementations of this interface are used in
 * {@link KerberosClient} to validate a Kerberos/SPNEGO
 * Ticket.
 *
 * @author Garry Boyce
 * @since 1.8.10
 * @see KerberosClient
 * 
 * originally from spring-kerberos project
 */
public interface KerberosTicketValidator {

    /**
     * Validates a Kerberos/SPNEGO ticket.
     *
     * @param token Kerbeos/SPNEGO ticket
     * @return authenticated kerberos principal
     * @throws BadCredentialsException if the ticket is not valid
     */
    public KerberosTicketValidation validateTicket(byte[] token) throws BadCredentialsException;
}
