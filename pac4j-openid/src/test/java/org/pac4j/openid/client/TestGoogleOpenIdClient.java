/*
  Copyright 2012 - 2014 Jerome Leleu

   import org.pac4j.openid.profile.OpenIdProfile;
(the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.openid.client;

import java.util.Locale;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Protocol;
import org.pac4j.core.client.TestClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.openid.profile.google.GoogleOpenIdProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link GoogleOpenIdClient} class by simulating a complete authentication.
 * 
 * @author Stephane Gleizes
 * @since 1.4.1
 */
@SuppressWarnings("rawtypes")
public class TestGoogleOpenIdClient extends TestClient implements TestsConstants {

    @Override
    protected Client getClient() {
        final GoogleOpenIdClient client = new GoogleOpenIdClient();
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("Email");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput passwd = form.getInputByName("Passwd");
        passwd.setValueAttribute("testpwdscribeup78");
        final HtmlSubmitInput submit = form.getInputByName("signIn");

        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(GoogleOpenIdProfile.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final GoogleOpenIdProfile profile = (GoogleOpenIdProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        final String id = "AItOawmMrzkgh-RhXW-d0iQ16ybkKpReh7g-hQQ";
        assertEquals("https://www.google.com/accounts/o8/id?id=" + id, profile.getId());
        assertEquals(GoogleOpenIdProfile.class.getSimpleName() + UserProfile.SEPARATOR
                + "https://www.google.com/accounts/o8/id?id=" + id, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GoogleOpenIdProfile.class));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
                Gender.UNSPECIFIED, Locale.FRANCE, null, null, "FR");
        assertEquals(5, profile.getAttributes().size());
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.OPENID;
    }
}
