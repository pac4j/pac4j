package org.pac4j.kerberos.credentials;

import lombok.ToString;
import org.pac4j.core.credentials.AuthenticationCredentials;

import java.nio.charset.StandardCharsets;

/**
 * Credentials containing the kerberos ticket.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
@ToString
public class KerberosCredentials extends AuthenticationCredentials {
    private byte[] kerberosTicket;
    /**
     *
     */
    private static final long serialVersionUID = -4264156105410684508L;

    public KerberosCredentials(byte[] kerberosTicket) {
        this.kerberosTicket = kerberosTicket.clone();
    }

    public String getKerberosTicketAsString() {
        return getTicketAsString(kerberosTicket);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        var that = (KerberosCredentials) o;

        return !(kerberosTicket != null ? !getTicketAsString(kerberosTicket).equals(getTicketAsString(that.kerberosTicket))
            : that.kerberosTicket != null);
    }

    @Override
    public int hashCode() {
        return kerberosTicket != null ? getTicketAsString(kerberosTicket).hashCode() : 0;
    }

    private String getTicketAsString(byte[] kerberosTicket) {
        return new String(kerberosTicket, StandardCharsets.UTF_8);
    }

    public byte[] getKerberosTicket() {
        return kerberosTicket.clone();
    }
}
