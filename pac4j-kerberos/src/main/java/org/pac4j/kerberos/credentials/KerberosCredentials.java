package org.pac4j.kerberos.credentials;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.HttpCredentials;

/**
 * Credentials containing the kerberos ticket.
 * 
 * @author Garry Boyce
 * @since 1.8.10
 */
public class KerberosCredentials extends HttpCredentials {
    private byte[]            kerberosTicket;
    /**
     * 
     */
    private static final long serialVersionUID = -4264156105410684508L;

    public KerberosCredentials(byte[] kerberosTicket, String clientName) {
        this.kerberosTicket = kerberosTicket;
        this.setClientName(clientName);
    }

    public byte[] getKerberosTicket() {
        return kerberosTicket;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "token", this.kerberosTicket, "clientName", getClientName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        KerberosCredentials that = (KerberosCredentials) o;

        return !(kerberosTicket != null ? !kerberosTicket.equals(that.kerberosTicket) : that.kerberosTicket != null);

    }

    @Override
    public int hashCode() {
        return kerberosTicket != null ? kerberosTicket.hashCode() : 0;
    }

    @Override
    public void clear() {
        this.kerberosTicket = null;
        this.setClientName(null);
        this.setUserProfile(null);
    }

}
