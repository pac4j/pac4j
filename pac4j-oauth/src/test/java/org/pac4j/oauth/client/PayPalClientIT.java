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
package org.pac4j.oauth.client;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.profile.paypal.PayPalAddress;
import org.pac4j.oauth.profile.paypal.PayPalProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link PayPalClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalClientIT extends OAuthClientIT {
    
    @Override
    public void testClone() {
        final PayPalClient oldClient = new PayPalClient();
        oldClient.setScope(SCOPE);
        final PayPalClient client = (PayPalClient) internalTestClone(oldClient);
        assertEquals(oldClient.getScope(), client.getScope());
    }
    
    public void testMissingFields() {
        final PayPalClient client = (PayPalClient) getClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final PayPalClient payPalClient = new PayPalClient(
                                                           "ARQFlBAOdRsb1NhZlutHT_PORP2F-TQpU-Laz-osaBwAHUIBIdg-C8DEsTWY",
                                                           "EAMZPBBfYJGeCBHYkm30xqC-VZ1kePnWZzPLdXyzY43rh-q0OQUH5eucXI6R");
        payPalClient.setCallbackUrl(PAC4J_BASE_URL);
        return payPalClient;
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("email");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("a1z2e3r4!$");
        final HtmlSubmitInput submit = form.getInputByName("_eventId_submit");
        final HtmlPage confirmPage = submit.click();
        final HtmlAnchor anchor = (HtmlAnchor) confirmPage.getElementById("continueButtonLink");
        final HtmlPage confirmPage2 = anchor.click();
        final String content = confirmPage2.asXml();
        final String url = StringUtils.substringBetween(content, "redirectUrl = \"", "\";");
        final HtmlPage callbackPage = webClient.getPage(url);
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(PayPalProfile.class);
        kryo.register(PayPalAddress.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final PayPalProfile profile = (PayPalProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("YAxf5WKSFn4BG_l3wqcBJUSObQTG1Aww5FY0EDf_ccw", profile.getId());
        assertEquals(PayPalProfile.class.getSimpleName() + UserProfile.SEPARATOR
                     + "YAxf5WKSFn4BG_l3wqcBJUSObQTG1Aww5FY0EDf_ccw", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), PayPalProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Test", "ScribeUP", "Test ScribeUP", null,
                            Gender.UNSPECIFIED, Locale.FRANCE, null, null, "Europe/Berlin");
        final PayPalAddress address = profile.getAddress();
        assertEquals("FR", address.getCountry());
        assertEquals("Paris", address.getLocality());
        assertEquals("75001", address.getPostalCode());
        assertEquals("Adr1", address.getStreetAddress());
        final Locale language = profile.getLanguage();
        assertEquals(Locale.FRANCE, language);
        assertEquals(9, profile.getAttributes().size());
    }
}
