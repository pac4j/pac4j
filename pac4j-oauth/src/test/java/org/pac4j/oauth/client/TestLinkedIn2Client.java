/*
  Copyright 2012 - 2013 Jerome Leleu

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

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2AttributesDefinition;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Location;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link LinkedIn2Client} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class TestLinkedIn2Client extends TestOAuthClient {
    
    @Override
    public void testClone() {
        final LinkedIn2Client oldClient = new LinkedIn2Client();
        oldClient.setScope(SCOPE);
        oldClient.setFields(FIELDS);
        final LinkedIn2Client client = (LinkedIn2Client) internalTestClone(oldClient);
        assertEquals(oldClient.getScope(), client.getScope());
        assertEquals(oldClient.getFields(), client.getFields());
    }
    
    public void testMissingScope() {
        final LinkedIn2Client client = (LinkedIn2Client) getClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }
    
    public void testMissingFields() {
        final LinkedIn2Client client = (LinkedIn2Client) getClient();
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final LinkedIn2Client client = new LinkedIn2Client();
        client.setKey("gsqj8dn56ayn");
        client.setSecret("kUFAZ2oYvwMQ6HFl");
        client.setScope("r_fullprofile r_emailaddress r_contactinfo");
        client.setCallbackUrl(GOOGLE_URL);
        return client;
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getFormByName("oauth2SAuthorizeForm");
        final HtmlTextInput email = form.getInputByName("session_key");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput password = form.getInputByName("session_password");
        password.setValueAttribute("testpwdscribeup");
        final HtmlSubmitInput submit = form.getInputByName("authorize");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(LinkedIn2Profile.class);
        kryo.register(LinkedIn2Location.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final LinkedIn2Profile profile = (LinkedIn2Profile) userProfile;
        logger.debug("profile : {}", profile);
        assertEquals("JJjS_5BOzW", profile.getId());
        assertEquals(LinkedIn2Profile.class.getSimpleName() + UserProfile.SEPARATOR + "JJjS_5BOzW",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedIn2Profile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "test", "scribeUp", null, null, Gender.UNSPECIFIED, null, null, null,
                            "Paris Area, France");
        LinkedIn2Location location = (LinkedIn2Location) profile.getAttribute(LinkedIn2AttributesDefinition.LOCATION);
        assertEquals("Paris Area, France", location.getName());
        assertEquals("fr", location.getCode());
        assertEquals(4, profile.getAttributes().size());
    }
}
