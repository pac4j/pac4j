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

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.profile.JsonList;
import org.pac4j.oauth.profile.facebook.*;
import org.pac4j.oauth.profile.ok.OkProfile;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class tests the {@link OkClient} class by simulating a complete authentication.
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkClientIT extends OAuthClientIT {

    @Override
    public void testClone() {
        final OkClient oldClient = new OkClient();
        oldClient.setPublicKey(PUBLIC_KEY);
        final OkClient client = (OkClient) internalTestClone(oldClient);
        assertEquals(oldClient.getPublicKey(), client.getPublicKey());
    }

    public void testMissingFields() {
        final OkClient client = (OkClient) getClient();
        client.setPublicKey(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    @Override
    protected Client getClient() {
        final OkClient okClient = new OkClient();
        okClient.setKey("1139019264");
        okClient.setPublicKey("CBAPAFOEEBABABABA");
        okClient.setSecret("479452FD7CA726DF558B4303");
        //FIXME
        okClient.setCallbackUrl("");
        return okClient;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("fr.email");
        //TODO change to real
        email.setValueAttribute("");
        final HtmlPasswordInput password = form.getInputByName("fr.password");
        //TODO  set to read
        password.setValueAttribute("");
        HtmlSubmitInput submit = (HtmlSubmitInput) form.getByXPath("//input[@type='submit']").get(0);
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(OkProfile.class);
        kryo.register(JsonList.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        //TODO change to real profile details
        final OkProfile profile = (OkProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("579337065742", profile.getId());
        assertEquals(FacebookProfile.class.getSimpleName() + UserProfile.SEPARATOR + "579337065742",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), OkProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "Vitaly", "Parkhomenko", "Vitaly Parkhomenko", "579337065742", Gender.MALE,
                new Locale("ru"), "http://i500.mycdn.me/res/stub_50x50.gif",
                "http://ok.ru/profile/579337065742", "Odessa, UKRAINE");

    }
}
