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
package org.pac4j.test.cas.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.CasClient.CasProtocol;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.CommonHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This class handles CAS callback (service callback post-authentication and proxy callback) for tests purpose.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CallbackController extends AbstractController {
    
    private final Clients clients = new Clients();
    
    private static final String CAS_BASE_URL = "http://localhost:8080/cas/";
    
    private static final String SERVICE_URL = "http://www.pac4j.org/";
    
    public CallbackController() {
        final CasClient casClient = new CasClient();
        casClient.setCasLoginUrl(CAS_BASE_URL + "login");
        casClient.setCasProtocol(CasProtocol.CAS20);
        final CasProxyReceptor casProxyReceptor = new CasProxyReceptor();
        casClient.setCasProxyReceptor(casProxyReceptor);
        this.clients.setCallbackUrl(CAS_BASE_URL + "callback");
        this.clients.setClients(casClient, casProxyReceptor);
        this.clients.init();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) {
        
        final WebContext context = new J2EContext(request, response);
        
        final Client<CasCredentials, CasProfile> client = this.clients.findClient(context);
        
        CasCredentials credentials = null;
        try {
            credentials = client.getCredentials(context);
        } catch (final RequiresHttpAction e) {
        }
        // has credentials
        if (credentials != null) {
            // get user profile
            final CasProxyProfile casProxyProfile = (CasProxyProfile) client.getUserProfile(credentials, context);
            // get proxy ticket
            final String proxyTicket = casProxyProfile.getProxyTicketFor(SERVICE_URL);
            return new ModelAndView(new RedirectView(CommonHelper.addParameter(SERVICE_URL, "ticket", proxyTicket)));
        } else {
            // mode proxy
        }
        return null;
    }
}
