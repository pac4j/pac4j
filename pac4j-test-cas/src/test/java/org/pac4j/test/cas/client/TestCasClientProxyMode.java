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
package org.pac4j.test.cas.client;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.CasClient.CasProtocol;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class tests the {@link CasClient} class in proxy mode.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestCasClientProxyMode extends TestCasClient {
    
    private CasClient casClient;
    
    private static final String CAS_URL = "http://localhost:8080/cas/";
    
    private static final String CALLBACK_URL = CAS_URL + "callback?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=";
    
    private static final String SERVICE_URL = PAC4J_BASE_URL;
    
    @Override
    protected CasProtocol getCasProtocol() {
        return CasProtocol.CAS20_PROXY;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        this.casClient = new CasClient();
        this.casClient.setCallbackUrl(CALLBACK_URL + this.casClient.getName());
        this.casClient.setCasLoginUrl(CAS_URL + "login");
        this.casClient.setCasProtocol(getCasProtocol());
        this.casClient.setAcceptAnyProxy(true);
        return this.casClient;
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final String callbackUrl = super.getCallbackUrl(webClient, authorizationPage);
        logger.debug("callbackUrl : {}", callbackUrl);
        
        this.casClient.setCallbackUrl(SERVICE_URL);
        this.casClient.reinit();
        
        return callbackUrl;
    }
}
