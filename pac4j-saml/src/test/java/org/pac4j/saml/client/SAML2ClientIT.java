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

package org.pac4j.saml.client;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.pac4j.core.client.ClientIT;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.storage.HttpSessionStorageFactory;
import org.pac4j.saml.util.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;

public abstract class SAML2ClientIT extends ClientIT implements TestsConstants {

    public SAML2ClientIT() {
        assertNotNull(Configuration.getParserPool());
        assertNotNull(Configuration.getMarshallerFactory());
        assertNotNull(Configuration.getUnmarshallerFactory());
        assertNotNull(Configuration.getBuilderFactory());
    }

    @Test
    public void testSPMetadata() {
        final SAML2Client client = getClient();
        client.init();
        final String spMetadata = client.getServiceProviderMetadataResolver().getMetadata();
        assertTrue(spMetadata.contains("entityID=\"" + client.getServiceProviderResolvedEntityId() + "\""));
        assertTrue(spMetadata
                .contains("<md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\""
                        + getCallbackUrl() + "\""));
    }

    @Test
    public void testCustomSpEntityId() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");
        client.init();
        final String spMetadata = client.getServiceProviderMetadataResolver().getMetadata();
        assertTrue(spMetadata.contains("entityID=\"http://localhost:8080/callback\""));
        assertTrue(spMetadata
                .contains("<md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\""
                        + getCallbackUrl() + "\""));
    }

    @Override
    protected final ClientType getClientType() {
        return ClientType.SAML_PROTOCOL;
    }

    @Override
    protected final void updateContextForAuthn(final WebClient webClient, final HtmlPage authorizationPage, final J2EContext context)
            throws Exception {
        if (authorizationPage.getForms().isEmpty()) {
            throw new SAMLException("Authorization page " + authorizationPage.getUrl() + " does not produce any forms");
        }

        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("j_username");
        email.setValueAttribute("myself");
        final HtmlPasswordInput password = form.getInputByName("j_password");
        password.setValueAttribute("myself");

        HtmlPage callbackPage;

        try {
            final HtmlSubmitInput submit = form.getInputByValue("Login");
            callbackPage = submit.click();
        } catch (final ElementNotFoundException e) {
            final HtmlButton btn = form.getButtonByName("_eventId_proceed");
            callbackPage = btn.click();
        }

         ;
        try {
            final String samlResponse = ((HtmlInput) callbackPage.getElementByName("SAMLResponse")).getValueAttribute();
            final String relayState = ((HtmlInput) callbackPage.getElementByName("RelayState")).getValueAttribute();

            final MockHttpServletRequest request = (MockHttpServletRequest) context.getRequest();
            request.addParameter("SAMLResponse", samlResponse);
            request.addParameter("RelayState", relayState);
            request.setMethod("POST");
            request.setRequestURI(callbackPage.getForms().get(0).getActionAttribute());
        } catch (final ElementNotFoundException e) {
            throw new SAMLException("Saml response and/or relay state not found", e);
        }
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 POST Binding");
    }

    @Override
    protected final void registerForKryo(final Kryo kryo) {
        kryo.register(SAML2Profile.class);
    }

    @Override
    protected final void verifyProfile(final UserProfile userProfile) {
        final SAML2Profile profile = (SAML2Profile) userProfile;
        assertEquals("[Member, Staff]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.1").toString());
        assertEquals("[myself]", profile.getAttribute("urn:oid:0.9.2342.19200300.100.1.1").toString());
        assertEquals("[Me Myself And I]", profile.getAttribute("urn:oid:2.5.4.3").toString());
        assertEquals("[myself@testshib.org]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6").toString());
        assertEquals("[555-5555]", profile.getAttribute("urn:oid:2.5.4.20").toString());
        assertEquals("[Member@testshib.org, Staff@testshib.org]",
                profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.9").toString());
        assertEquals("[urn:mace:dir:entitlement:common-lib-terms]",
                profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.7").toString());
        assertEquals("[Me Myself]", profile.getAttribute("urn:oid:2.5.4.42").toString());
        assertEquals("[And I]", profile.getAttribute("urn:oid:2.5.4.4").toString());
    }

    @Override
    protected final SAML2Client getClient() {

        final SAML2ClientConfiguration cfg =
                new SAML2ClientConfiguration("resource:samlKeystore.jks",
                        "pac4j-demo-passwd",
                        "pac4j-demo-passwd",
                        "resource:testshib-providers.xml");

        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setDestinationBindingType(getDestinationBindingType());
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataPath(new File("target", "sp-metadata.xml").getAbsolutePath());
        cfg.setSamlMessageStorageFactory(new HttpSessionStorageFactory());
        final SAML2Client saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl(getCallbackUrl());

        return saml2Client;
    }

    protected abstract String getCallbackUrl();

    protected abstract String getDestinationBindingType();
}
