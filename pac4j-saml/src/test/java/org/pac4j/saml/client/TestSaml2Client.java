/*
  Copyright 2012 -2014 Michael Remond

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

import java.io.File;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.NotImplementedException;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Protocol;
import org.pac4j.core.client.TestClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.profile.Saml2Profile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public final class TestSaml2Client extends TestClient implements TestsConstants {

    public void testSPMetadata() {

        Saml2Client client = (Saml2Client) getClient();
        String spMetadata = client.printClientMetadata();
        assertTrue(spMetadata.contains("entityID=\"http://localhost:8080/callback?client_name=Saml2Client\""));
        assertTrue(spMetadata
                .contains("<md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"http://localhost:8080/callback?client_name=Saml2Client\""));

    }

    @Override
    protected Protocol getProtocol() {
        // TODO Auto-generated method stub
        return Protocol.SAML;
    }

    @Override
    protected Client getClient() {
        final Saml2Client saml2Client = new Saml2Client();
        saml2Client.setKeystorePath("resource:samlKeystore.jks");
        saml2Client.setKeystorePassword("pac4j-demo-passwd");
        saml2Client.setPrivateKeyPassword("pac4j-demo-passwd");
        saml2Client.setIdpMetadataPath("resource:testshib-providers.xml");
        saml2Client.setCallbackUrl("http://localhost:8080/callback?client_name=Saml2Client");
        saml2Client.setMaximumAuthenticationLifetime(3600);
        return saml2Client;
    }

    @Override
    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client client, final MockWebContext context)
            throws Exception {
        final BaseClient baseClient = (BaseClient) client;
        // force immediate redirection for tests
        baseClient.redirect(context, true, false);
        File redirectFile = File.createTempFile("pac4j-saml2", ".html");
        FileWriterWithEncoding writer = new FileWriterWithEncoding(redirectFile, "UTF-8");
        writer.write(context.getResponseContent());
        writer.close();
        logger.debug("redirectPage path : {}", redirectFile.getPath());
        final HtmlPage redirectPage = webClient.getPage(redirectFile.toURI().toURL());
        final HtmlForm form = redirectPage.getForms().get(0);
        final HtmlSubmitInput submit = (HtmlSubmitInput) form.getElementsByAttribute("input", "type", "submit").get(0);
        return submit.click();
    }

    @Override
    protected void updateContextForAuthn(WebClient webClient, HtmlPage authorizationPage, MockWebContext context)
            throws Exception {
        final MockWebContext mockWebContext = context;
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("j_username");
        email.setValueAttribute("myself");
        final HtmlPasswordInput password = form.getInputByName("j_password");
        password.setValueAttribute("myself");
        final HtmlSubmitInput submit = form.getInputByValue("Login");
        final HtmlPage callbackPage = submit.click();
        String samlResponse = ((HtmlInput) callbackPage.getElementByName("SAMLResponse")).getValueAttribute();
        String relayState = ((HtmlInput) callbackPage.getElementByName("RelayState")).getValueAttribute();
        mockWebContext.addRequestParameter("SAMLResponse", samlResponse);
        mockWebContext.addRequestParameter("RelayState", relayState);
        mockWebContext.setRequestMethod("POST");
        mockWebContext.setFullRequestURL(callbackPage.getForms().get(0).getActionAttribute());
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 POST Binding");
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(Saml2Profile.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        Saml2Profile profile = (Saml2Profile) userProfile;
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
}
