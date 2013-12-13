/*
  Copyright 2012 - 2013 Jerome Leleu

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

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Protocol;
import org.pac4j.core.client.TestClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.openid.profile.myopenid.MyOpenIdProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 * This class tests the {@link MyOpenIdClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
@SuppressWarnings("rawtypes")
public class TestMyOpenIdClient extends TestClient implements TestsConstants {
    
    public void testClone() {
        final MyOpenIdClient oldClient = new MyOpenIdClient();
        oldClient.setUserParameterName(PARAMETER_NAME);
        final MyOpenIdClient client = (MyOpenIdClient) internalTestClone(oldClient);
        assertEquals(oldClient.getUserParameterName(), client.getUserParameterName());
    }
    
    public void testMissingUserParameterName() {
        final MyOpenIdClient client = (MyOpenIdClient) getClient();
        client.setUserParameterName(null);
        TestsHelper.initShouldFail(client, "userParameterName cannot be blank");
    }
    
    public void testMissingUser() {
        final MyOpenIdClient client = (MyOpenIdClient) getClient();
        try {
            client.getRedirectionUrl(MockWebContext.create(), true);
            fail("should fail because of missing OpenID user");
        } catch (final TechnicalException e) {
            assertEquals("openIdUser cannot be blank", e.getMessage());
        }
    }
    
    @Override
    protected Client getClient() {
        final MyOpenIdClient client = new MyOpenIdClient();
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }
    
    @Override
    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client client, final WebContext context)
        throws Exception {
        final MockWebContext mockWebContext = (MockWebContext) context;
        mockWebContext
            .addRequestParameter(MyOpenIdClient.DEFAULT_USER_PARAMETER_NAME, "http://testpac4j.myopenid.com/");
        return super.getRedirectionPage(webClient, client, mockWebContext);
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("testpwdpac4j");
        final HtmlSubmitInput submit = form.getInputByValue("Sign In");
        final HtmlPage callbackPage = submit.click();
        /*form = confirmPage.getForms().get(0);
        HtmlButton continueButton = (HtmlButton) form.getElementById("continue-button");
        HtmlPage callbackPage = continueButton.click();*/
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(MyOpenIdProfile.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final MyOpenIdProfile profile = (MyOpenIdProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("http://testpac4j.myopenid.com/", profile.getId());
        assertEquals(MyOpenIdProfile.class.getSimpleName() + UserProfile.SEPARATOR + "http://testpac4j.myopenid.com/",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), MyOpenIdProfile.class));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", null, null, "Test pac4j", null, Gender.UNSPECIFIED,
                            null, null, null, null);
        assertEquals(2, profile.getAttributes().size());
    }
    
    @Override
    protected boolean isCancellable() {
        return true;
    }
    
    @Override
    protected String getCallbackUrlForCancel(final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlSubmitInput submit = form.getInputByValue("Cancel");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected Protocol getProtocol() {
        return Protocol.OPENID;
    }
}
