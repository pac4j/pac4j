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
package org.pac4j.cas.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents a CAS credentials through a service ticket.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasCredentials extends Credentials {
    
    private static final long serialVersionUID = -6006976366005458716L;
    
    private String serviceTicket;
    
    public CasCredentials(final String serviceTicket, final String clientName) {
        this.serviceTicket = serviceTicket;
        setClientName(clientName);
    }
    
    public String getServiceTicket() {
        return this.serviceTicket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CasCredentials that = (CasCredentials) o;

        return !(serviceTicket != null ? !serviceTicket.equals(that.serviceTicket) : that.serviceTicket != null);

    }

    @Override
    public int hashCode() {
        return serviceTicket != null ? serviceTicket.hashCode() : 0;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "serviceTicket", this.serviceTicket, "clientName",
                                     getClientName());
    }
}
