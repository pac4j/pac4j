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

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Protocol;
import org.pac4j.core.client.TestClient;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link CasOAuthWrapperClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestCasOAuthWrapperClient extends TestClient {
    
    private static final String USERNAME = "leleuj";
    
    private static final String CAS_SERVER_URL = "http://casserverurl/oauth2.0";
    
    public void testClone() {
        final CasOAuthWrapperClient oldClient = new CasOAuthWrapperClient();
        oldClient.setCasOAuthUrl(CAS_SERVER_URL);
        oldClient.setSpringSecurityCompliant(true);
        final CasOAuthWrapperClient client = (CasOAuthWrapperClient) internalTestClone(oldClient);
        assertEquals(oldClient.getCasOAuthUrl(), client.getCasOAuthUrl());
        assertEquals(oldClient.isSpringSecurityCompliant(), client.isSpringSecurityCompliant());
    }
    
    public void testMissingCasOAuthUrl() {
        final CasOAuthWrapperClient client = (CasOAuthWrapperClient) getClient();
        client.setCasOAuthUrl(null);
        TestsHelper.initShouldFail(client, "casOAuthUrl cannot be blank");
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final CasOAuthWrapperClient casOAuthWrapperClient = new CasOAuthWrapperClient();
        casOAuthWrapperClient.setKey("my_key");
        casOAuthWrapperClient.setSecret("my_secret");
        casOAuthWrapperClient.setCallbackUrl(PAC4J_BASE_URL);
        casOAuthWrapperClient.setCasOAuthUrl("http://localhost:8080/cas/oauth2.0");
        return casOAuthWrapperClient;
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput username = form.getInputByName("username");
        username.setValueAttribute(USERNAME);
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute(USERNAME);
        final HtmlSubmitInput submit = form.getInputByName("submit");
        final HtmlPage confirmPage = submit.click();
        final HtmlAnchor link = confirmPage.getAnchorByName("allow");
        final String callbackUrl = link.getHrefAttribute();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(CasOAuthWrapperProfile.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final CasOAuthWrapperProfile profile = (CasOAuthWrapperProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals(USERNAME, profile.getId());
        assertEquals(CasOAuthWrapperProfile.class.getSimpleName() + UserProfile.SEPARATOR + USERNAME,
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CasOAuthWrapperProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertEquals("uid", profile.getAttribute("uid"));
        assertEquals("eduPersonAffiliation", profile.getAttribute("eduPersonAffiliation"));
        assertEquals("groupMembership", profile.getAttribute("groupMembership"));
        assertEquals(4, profile.getAttributes().size());
    }
    
    @Override
    protected Protocol getProtocol() {
        return Protocol.OAUTH;
    }
}
