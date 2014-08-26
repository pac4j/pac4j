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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.TestsConstants;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public final class RedirectSaml2ClientIT extends Saml2ClientIT implements TestsConstants {

    @Test
    public void testForceAuthIsSetForRedirectBinding() throws Exception {
        Saml2Client client = getClient();
        client.setForceAuth(true);
        WebContext context = MockWebContext.create();
        RedirectAction action = client.getRedirectAction(context, true, false);
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithRedirectBinding() throws Exception {
        Saml2Client client = getClient();
        client.setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        WebContext context = MockWebContext.create();
        RedirectAction action = client.getRedirectAction(context, true, false);
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("Comparison=\"exact\""));
    }
    
    @Test
    public void testRelayState() throws Exception {
        Saml2Client client = getClient();
        WebContext context = MockWebContext.create();
        context.setSessionAttribute(Saml2Client.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        RedirectAction action = client.getRedirectAction(context, true, false);
        assertTrue(action.getLocation().contains("RelayState=relayState"));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=Saml2Client";
    }
    
    @Override
    protected String getDestinationBindingType() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;  
    }


    @Override
    protected String getCallbackUrl(WebClient webClient, HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 Redirect Binding");
    }

    private String getInflatedAuthnRequest(String location) throws Exception {
        List<NameValuePair> pairs = URLEncodedUtils.parse(URI.create(location), "UTF-8");
        Inflater inflater = new Inflater(true);
        byte[] decodedRequest = Base64.decodeBase64(pairs.get(0).getValue());
        ByteArrayInputStream is = new ByteArrayInputStream(decodedRequest);
        InflaterInputStream inputStream = new InflaterInputStream(is, inflater);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.readLine();
    }
}
