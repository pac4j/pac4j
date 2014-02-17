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

import org.jasig.cas.client.validation.ProxyList;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.CasClient.CasProtocol;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Protocol;
import org.pac4j.core.client.TestClient;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class is an abstract test case for the {@link CasClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class TestCasClient extends TestClient {
    
    private static final String USERNAME = "leleuj";
    
    private static final String CAS_LOGIN_URL = "http://casserver/login";
    
    private static final String CAS_PREFIX_URL = "http://casserver/login";
    
    private static final CasProtocol CAS_PROTOCOL = CasProtocol.SAML;
    
    protected abstract CasProtocol getCasProtocol();
    
    public void testClone() {
        final CasClient oldClient = new CasClient();
        oldClient.setCasLoginUrl(CAS_LOGIN_URL);
        oldClient.setCasPrefixUrl(CAS_PREFIX_URL);
        oldClient.setCasProtocol(CAS_PROTOCOL);
        oldClient.setRenew(true);
        oldClient.setGateway(true);
        oldClient.setAcceptAnyProxy(true);
        final ProxyList proxyList = new ProxyList();
        oldClient.setAllowedProxyChains(proxyList);
        final CasProxyReceptor casProxyReceptor = new CasProxyReceptor();
        oldClient.setCasProxyReceptor(casProxyReceptor);
        final CasClient client = (CasClient) internalTestClone(oldClient);
        assertEquals(oldClient.getCasLoginUrl(), client.getCasLoginUrl());
        assertEquals(oldClient.getCasPrefixUrl(), client.getCasPrefixUrl());
        assertEquals(oldClient.getCasProtocol(), client.getCasProtocol());
        assertEquals(true, client.isRenew());
        assertEquals(true, client.isGateway());
        assertEquals(true, client.isAcceptAnyProxy());
        assertEquals(oldClient.getAllowedProxyChains(), client.getAllowedProxyChains());
        assertEquals(oldClient.getCasProxyReceptor(), client.getCasProxyReceptor());
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(PAC4J_BASE_URL);
        casClient.setCasLoginUrl("http://localhost:8080/cas/login");
        casClient.setCasProtocol(getCasProtocol());
        return casClient;
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput username = form.getInputByName("username");
        username.setValueAttribute(USERNAME);
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute(USERNAME);
        final HtmlSubmitInput submit = form.getInputByName("submit");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(CasProfile.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final CasProfile profile = (CasProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals(USERNAME, profile.getId());
        assertEquals(CasProfile.class.getSimpleName() + UserProfile.SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CasProfile.class));
        if (getCasProtocol() == CasProtocol.SAML) {
            assertEquals("uid", profile.getAttribute("uid"));
            assertEquals("eduPersonAffiliation", profile.getAttribute("eduPersonAffiliation"));
            assertEquals("groupMembership", profile.getAttribute("groupMembership"));
            assertEquals(3, profile.getAttributes().size());
        } else {
            assertEquals(0, profile.getAttributes().size());
        }
    }
    
    @Override
    protected Protocol getProtocol() {
        return Protocol.CAS;
    }
}
