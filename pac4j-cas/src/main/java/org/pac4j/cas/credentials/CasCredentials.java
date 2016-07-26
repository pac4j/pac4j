package org.pac4j.cas.credentials;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents a CAS credentials through a service ticket.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasCredentials extends TokenCredentials {
    
    private static final long serialVersionUID = -6006976366005458716L;

    public CasCredentials(final String serviceTicket, final String clientName) {
        super(serviceTicket, clientName);
    }
    
    public String getServiceTicket() {
        return getToken();
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "serviceTicket", getToken(), "clientName", getClientName());
    }
}
