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

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.exceptions.SAMLException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URL;

public final class PostSAML2ClientIT extends SAML2ClientIT implements TestsConstants {

    public PostSAML2ClientIT() {
        super();
    }

    @Override
    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client<?, ?> client,
            final J2EContext context) throws Exception {
        // force immediate redirection for tests
        client.redirect(context, true);
        final File redirectFile = File.createTempFile("pac4j-saml2", ".html");
        final FileWriterWithEncoding writer = new FileWriterWithEncoding(redirectFile, "UTF-8");

        final MockHttpServletResponse response = (MockHttpServletResponse) context.getResponse();
        writer.write(response.getContentAsString());
        writer.close();
        logger.debug("redirectPage path : {}", redirectFile.getPath());
        final HtmlPage redirectPage = webClient.getPage(redirectFile.toURI().toURL());
        if (redirectPage.getForms().isEmpty()) {
            throw new SAMLException("Page " + redirectPage.getUrl() + " did not produce a form");
        }
        final HtmlForm form = redirectPage.getForms().get(0);

        final HtmlSubmitInput submit = (HtmlSubmitInput) form.getElementsByAttribute("input", "type", "submit").get(0);
        return submit.click();
    }

    private String getDecodedAuthnRequest(final String content) throws Exception {
        final StringWebResponse response = new StringWebResponse(content, new URL("http://localhost:8080/"));
        final WebClient webClient = new WebClient();
        final HtmlPage page = HTMLParser.parseHtml(response, webClient.getCurrentWindow());
        if (page.getForms().isEmpty()) {
            throw new SAMLException("Page " + page.getUrl() + " did not produce a form");
        }
        final HtmlForm form = page.getForms().get(0);
        final HtmlInput samlRequest = form.getInputByName("SAMLRequest");
        return new String(Base64.decodeBase64(samlRequest.getValueAttribute()));
    }

    @Test
    public void testCustomSpEntityIdForPostBinding() throws Exception {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(getDecodedAuthnRequest(action.getContent())
                .contains(
                        "<saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/callback</saml2:Issuer>"));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() throws Exception {
        final SAML2Client client =  getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() throws Exception {
        final SAML2Client client =  getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testRelayState() throws RequiresHttpAction {
        final SAML2Client client = getClient();
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.setSessionAttribute(SAML2Client.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(action.getContent().contains("<input type=\"hidden\" name=\"RelayState\" value=\"relayState\"/>"));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getDestinationBindingType() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 POST Binding");
    }

    @Override
    protected HttpServletRequest getHttpServletRequest() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        return request;
    }
}
