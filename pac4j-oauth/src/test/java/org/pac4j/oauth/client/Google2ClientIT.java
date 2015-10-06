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
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.client.Google2Client.Google2Scope;
import org.pac4j.oauth.profile.JsonList;
import org.pac4j.oauth.profile.google2.Google2Email;
import org.pac4j.oauth.profile.google2.Google2Profile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link Google2Client} class by simulating a complete authentication.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2ClientIT extends OAuthClientIT {

    @Override
    public void testClone() {
        final Google2Client oldClient = new Google2Client();
        oldClient.setScope(Google2Scope.EMAIL_AND_PROFILE);
        final Google2Client client = (Google2Client) internalTestClone(oldClient);
        assertEquals(oldClient.getScope(), client.getScope());
    }

    public void testMissingScope() {
        final Google2Client client = (Google2Client) getClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be null");
    }

    public void testDefaultScope() throws RequiresHttpAction {
        final Google2Client google2Client = new Google2Client();
        google2Client.setKey(KEY);
        google2Client.setSecret(SECRET);
        google2Client.setCallbackUrl(CALLBACK_URL);
        google2Client.redirect(MockWebContext.create(), false);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final Google2Client google2Client = new Google2Client();
        google2Client.setKey("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        google2Client.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        google2Client.setCallbackUrl(PAC4J_BASE_URL);
        google2Client.setScope(Google2Scope.EMAIL_AND_PROFILE);
        return google2Client;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("Email");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput passwd = form.getInputByName("Passwd");
        passwd.setValueAttribute("testpwdscribeup91");
        final HtmlSubmitInput submit = form.getInputByName("signIn");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(Google2Profile.class);
        kryo.register(JsonList.class);
        kryo.register(Google2Email.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final Google2Profile profile = (Google2Profile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(Google2Profile.class.getSimpleName() + UserProfile.SEPARATOR + "113675986756217860428",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), Google2Profile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
                Gender.MALE, Locale.ENGLISH,
                "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
                "https://plus.google.com/113675986756217860428", null);
        assertNull(profile.getBirthday());
        assertTrue(profile.getEmails() != null && profile.getEmails().size() == 1);
        assertEquals(9, profile.getAttributes().size());
    }
}
