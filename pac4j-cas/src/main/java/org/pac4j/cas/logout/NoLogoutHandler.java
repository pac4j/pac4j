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

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.WebContext;

/**
 * This class handles logout but does not perform it.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class NoLogoutHandler implements LogoutHandler {
    
    private static final String LOGOUT_REQUEST_PARAMETER = "logoutRequest";
    
    public boolean isTokenRequest(final WebContext context) {
        return context.getRequestParameter(CasClient.SERVICE_TICKET_PARAMETER) != null;
    }
    
    public boolean isLogoutRequest(final WebContext context) {
        return "POST".equals(context.getRequestMethod())
               && context.getRequestParameter(LOGOUT_REQUEST_PARAMETER) != null;
    }
    
    public void recordSession(final WebContext context, final String ticket) {
    }
    
    public void destroySession(final WebContext context) {
    }
}
