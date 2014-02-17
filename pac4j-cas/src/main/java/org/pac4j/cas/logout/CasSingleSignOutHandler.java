/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.cas.logout;

import org.jasig.cas.client.session.SingleSignOutHandler;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;

/**
 * This class is the logout handler for the {@link CasClient} class based on the {@link SingleSignOutHandler} class of the Jasig CAS client.<br />
 * It should only be used in J2E context.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasSingleSignOutHandler implements LogoutHandler {
    
    private final SingleSignOutHandler singleSignOutHandler;
    
    /**
     * Construct an instance from the default {@link SingleSignOutHandler} class.
     */
    public CasSingleSignOutHandler() {
        this.singleSignOutHandler = new SingleSignOutHandler();
    }
    
    /**
     * Construct an instance from a given {@link SingleSignOutHandler} class.
     * 
     * @param singleSignOutHandler
     */
    public CasSingleSignOutHandler(final SingleSignOutHandler singleSignOutHandler) {
        this.singleSignOutHandler = singleSignOutHandler;
    }
    
    public boolean isTokenRequest(final WebContext context) {
        final J2EContext j2eContext = (J2EContext) context;
        return this.singleSignOutHandler.isTokenRequest(j2eContext.getRequest());
    }
    
    public boolean isLogoutRequest(final WebContext context) {
        final J2EContext j2eContext = (J2EContext) context;
        return this.singleSignOutHandler.isLogoutRequest(j2eContext.getRequest());
    }
    
    public void recordSession(final WebContext context, final String ticket) {
        final J2EContext j2eContext = (J2EContext) context;
        this.singleSignOutHandler.recordSession(j2eContext.getRequest());
    }
    
    public void destroySession(final WebContext context) {
        final J2EContext j2eContext = (J2EContext) context;
        this.singleSignOutHandler.destroySession(j2eContext.getRequest());
    }
}
