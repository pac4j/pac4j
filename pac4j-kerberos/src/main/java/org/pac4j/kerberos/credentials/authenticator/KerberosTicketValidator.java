package org.pac4j.kerberos.credentials.authenticator;

import org.pac4j.kerberos.client.direct.DirectKerberosClient;

/**
 * Implementations of this interface are used in
 * {@link org.pac4j.kerberos.client.direct.DirectKerberosClient} to validate a Kerberos/SPNEGO
 * Ticket.
 *
 * @author Garry Boyce
 * @see DirectKerberosClient
 * <p>
 * originally from spring-kerberos project
 * @since 2.1.0
 */
@FunctionalInterface
public interface KerberosTicketValidator {

    /**
     * Validates a Kerberos/SPNEGO ticket.
     *
     * @param token Kerbeos/SPNEGO ticket
     * @return authenticated kerberos principal
     */
    KerberosTicketValidation validateTicket(byte[] token);
}
