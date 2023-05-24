package org.pac4j.kerberos.credentials;

import lombok.ToString;
import org.pac4j.core.credentials.Credentials;

import java.io.Serial;
import java.nio.charset.StandardCharsets;

/**
 * Credentials containing the kerberos ticket.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
@ToString
public class KerberosCredentials extends Credentials {
    private byte[] kerberosTicket;
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4264156105410684508L;

    /**
     * <p>Constructor for KerberosCredentials.</p>
     *
     * @param kerberosTicket an array of {@link byte} objects
     */
    public KerberosCredentials(byte[] kerberosTicket) {
        this.kerberosTicket = kerberosTicket.clone();
    }

    /**
     * <p>getKerberosTicketAsString.</p>
     *
     * @return a {@link String} object
     */
    public String getKerberosTicketAsString() {
        return getTicketAsString(kerberosTicket);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return kerberosTicket != null ? getTicketAsString(kerberosTicket).hashCode() : 0;
    }

    private String getTicketAsString(byte[] kerberosTicket) {
        return new String(kerberosTicket, StandardCharsets.UTF_8);
    }

    /**
     * <p>Getter for the field <code>kerberosTicket</code>.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getKerberosTicket() {
        return kerberosTicket.clone();
    }
}
