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
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.profile.ok.OkProfile;

import java.util.Locale;

/**
 * This class tests the {@link OkClient} class by simulating a complete authentication.
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkClientIT extends OAuthClientIT {
    /////////////////////////////////////////////
    /**
     * Real profile login.
     */
    private static final String OK_LOGIN = "";
    /**
     * Real profile password.
     */
    private static final String OK_PASSWORD = "";
    /////////////////////////////////////////////
    /**
     * Real profile id.
     */
    private static final String TEST_PROFILE_ID = "";
    /**
     * Real profile location.
     */
    private static final String TEST_LOCATION = "";
    /**
     * Real profile locale.
     */
    private static final java.lang.String TEST_LOCALE = "ru";
    /**
     * Real profile first name.
     */
    private static final String TEST_FIRST_NAME = "";
    /**
     * Real profile last name.
     */
    private static final String TEST_LAST_NAME = "";
    /**
     * Real profile picture url.
     */
    private static final String TEST_PROFILE_PICTURE_URL = "";
    /**
     * Application id.
     */
    /////////////////////////////////////////////
    private static final String TEST_APP_ID = "1139019264";
    /**
     * Application public key.
     */
    private static final String TEST_APP_PUBLIC_KEY = "CBAPAFOEEBABABABA";
    /**
     * Application secret key.
     */
    private static final String TEST_APP_SECRET_KEY = "479452FD7CA726DF558B4303";
    /////////////////////////////////////////////

    public void testMissingFields() {
        final OkClient client = (OkClient) getClient();
        client.setPublicKey(null);
        TestsHelper.initShouldFail(client, "publicKey cannot be blank");
    }

    @Override
    protected Client getClient() {
        final OkClient okClient = new OkClient();
        okClient.setKey(TEST_APP_ID);
        okClient.setPublicKey(TEST_APP_PUBLIC_KEY);
        okClient.setSecret(TEST_APP_SECRET_KEY);
        okClient.setCallbackUrl(PAC4J_URL);
        return okClient;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("fr.email");
        email.setValueAttribute(OK_LOGIN);
        final HtmlPasswordInput password = form.getInputByName("fr.password");
        password.setValueAttribute(OK_PASSWORD);
        HtmlSubmitInput submit = (HtmlSubmitInput) form.getByXPath("//input[@type='submit']").get(0);
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(OkProfile.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final OkProfile profile = (OkProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals(TEST_PROFILE_ID, profile.getId());
        assertEquals(OkProfile.class.getSimpleName() + UserProfile.SEPARATOR + TEST_PROFILE_ID,
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), OkProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(
                userProfile,
                null,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_FIRST_NAME + " " + TEST_LAST_NAME,
                TEST_PROFILE_ID,
                Gender.MALE,
                new Locale(TEST_LOCALE),
                TEST_PROFILE_PICTURE_URL,
                OkProfile.BASE_PROFILE_URL + TEST_PROFILE_ID,
                TEST_LOCATION);

    }
}
