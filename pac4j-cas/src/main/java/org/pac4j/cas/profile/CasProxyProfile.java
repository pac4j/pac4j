package org.pac4j.cas.profile;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the user profile for sites using CAS protocol with proxy capabilities.</p>
 * <p>It is returned by the {@link org.pac4j.cas.client.CasClient} coupled with the {@link org.pac4j.cas.client.CasProxyReceptor}.</p>
 * <p>After the CAS principal has been set through the {@link #setPrincipal(AttributePrincipal)} method, proxy tickets can be retrieved for
 * various CAS services by using the {@link #getProxyTicketFor(String)} method.</p>
 * 
 * @see org.pac4j.cas.client.CasClient
 * @see org.pac4j.cas.client.CasProxyReceptor
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasProxyProfile extends CasProfile {
    
    private static final long serialVersionUID = 4956675835922254493L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AttributePrincipal attributePrincipal = null;
    
    /**
     * Store the CAS principal.
     * 
     * @param attributePrincipal the principal with attributes
     */
    public void setPrincipal(final AttributePrincipal attributePrincipal) {
        this.attributePrincipal = attributePrincipal;
    }
    
    /**
     * Get a proxy ticket for a given service.
     * 
     * @param service the CAS service
     * @return the proxy ticket for the given service
     */
    public String getProxyTicketFor(final String service) {
        if (this.attributePrincipal != null) {
            logger.debug("Requesting PT from principal: {} and for service: {}", attributePrincipal, service);
            final String pt = this.attributePrincipal.getProxyTicketFor(service);
            logger.debug("Get PT: {}", pt);
            return pt;
        }
        return null;
    }
}
