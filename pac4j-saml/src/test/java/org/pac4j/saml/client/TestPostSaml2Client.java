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
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public final class TestPostSaml2Client extends TestSaml2Client implements TestsConstants {
    
    @Override
    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client<?,?> client, final MockWebContext context)
            throws Exception {
        // force immediate redirection for tests
        client.redirect(context, true, false);
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

    private String getDecodedAuthnRequest(String content) throws Exception {
        StringWebResponse response = new StringWebResponse(content, new URL("http://localhost:8080/"));
        WebClient webClient = new WebClient();
        HtmlPage page = HTMLParser.parseHtml(response, webClient.getCurrentWindow());
        HtmlForm form = page.getForms().get(0);
        HtmlInput samlRequest = form.getInputByName("SAMLRequest");
        return new String(Base64.decodeBase64(samlRequest.getValueAttribute()));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() throws Exception {
        Saml2Client client = (Saml2Client) getClient();
        client.setForceAuth(true);
        WebContext context = MockWebContext.create();
        RedirectAction action = client.getRedirectAction(context, true, false);
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() throws Exception {
        Saml2Client client = (Saml2Client) getClient();
        client.setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        WebContext context = MockWebContext.create();
        RedirectAction action = client.getRedirectAction(context, true, false);
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }
    
    @Test
    public void testRelayState() throws RequiresHttpAction {
        Saml2Client client = (Saml2Client) getClient();
        WebContext context = MockWebContext.create();
        context.setSessionAttribute(Saml2Client.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        RedirectAction action = client.getRedirectAction(context, true, false);
        assertTrue(action.getContent().contains("<input type=\"hidden\" name=\"RelayState\" value=\"relayState\"/>"));
    }
    
    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=Saml2Client";
    }
    
    @Override
    protected String getDestinationBindingType() {
        return SAMLConstants.SAML2_POST_BINDING_URI;  
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 POST Binding");
    }
}
