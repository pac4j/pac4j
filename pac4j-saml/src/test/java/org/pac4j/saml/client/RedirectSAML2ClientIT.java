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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.TestsConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public final class RedirectSAML2ClientIT extends SAML2ClientIT implements TestsConstants {

    public RedirectSAML2ClientIT() {
        super();
    }

    @Test
    public void testCustomSpEntityIdForRedirectBinding() throws Exception {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");

        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        final String inflated = getInflatedAuthnRequest(action.getLocation());

        assertTrue(inflated.contains(
                "<saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/callback</saml2:Issuer>"));
    }

    @Test
    public void testForceAuthIsSetForRedirectBinding() throws Exception {
        final SAML2Client client = getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithRedirectBinding() throws Exception {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testNameIdPolicyFormat() throws Exception{
        final SAML2Client client = getClient();
        client.getConfiguration().setNameIdPolicyFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);
        final String loc = action.getLocation();
        assertTrue(getInflatedAuthnRequest(loc).contains("<saml2p:NameIDPolicy AllowCreate=\"true\" " +
                "Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\" " +
                "xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\"/></saml2p:AuthnRequest>"));
    }

    @Test
    public void testAuthnContextClassRef() throws Exception {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        client.getConfiguration().setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context, true);

        final String checkClass = "<saml2p:RequestedAuthnContext Comparison=\"exact\" " +
                "xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\"><saml2:AuthnContextClassRef " +
                "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef>" +
                "</saml2p:RequestedAuthnContext>";

        logger.debug("Checking for authn class {}", checkClass);
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains(checkClass));
    }

    @Test
    public void testRelayState() throws Exception {
        final SAML2Client client = getClient();
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.setSessionAttribute(SAML2Client.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final RedirectAction action = client.getRedirectAction(context, true);
        assertTrue(action.getLocation().contains("RelayState=relayState"));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getDestinationBindingType() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 Redirect Binding");
    }

    private String getInflatedAuthnRequest(final String location) throws Exception {
        final List<NameValuePair> pairs = URLEncodedUtils.parse(URI.create(location), "UTF-8");
        final Inflater inflater = new Inflater(true);
        final byte[] decodedRequest = Base64.decodeBase64(pairs.get(0).getValue());
        final ByteArrayInputStream is = new ByteArrayInputStream(decodedRequest);
        final InflaterInputStream inputStream = new InflaterInputStream(is, inflater);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        final StringBuilder bldr = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            bldr.append(line);
        }

        logger.debug("Inflated authn request {}", bldr.toString());
        return bldr.toString();
    }
}
