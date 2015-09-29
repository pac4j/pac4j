/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.cas.profile;

import org.jasig.cas.client.authentication.AttributePrincipal;

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
            return this.attributePrincipal.getProxyTicketFor(service);
        }
        return null;
    }
}
