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
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;

/**
 * This class tests the {@link BitbucketClient} class by simulating a complete 
 * authentication.
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketClientIT extends OAuthClientIT {

    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        BitbucketClient client = new BitbucketClient();
        client.setKey("bjEt8BMpLwFDqZUvp6");
        client.setSecret("NN6fVXRTcV2qYVejVLZqxBRqHgn3ygD4");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected String getCallbackUrl(WebClient webClient, HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getHtmlElementById("login-form");
        HtmlTextInput username = form.getInputByName("username");
        username.setValueAttribute("testscribeup");
        HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("testpwdscribeup78");
        HtmlButton submit = form.getButtonByName("submit");
        HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        BitbucketProfile profile = (BitbucketProfile) userProfile;
        assertEquals("testscribeup", profile.getUsername());
        assertEquals("Test", profile.getFirstName());
        assertEquals("Scribeup", profile.getFamilyName());
        assertEquals("Test Scribeup", profile.getDisplayName());
        assertFalse(profile.isTeam());
        assertTrue(profile.getPictureUrl().startsWith("https://bitbucket.org/account/testscribeup/avatar"));
        assertEquals("/1.0/users/testscribeup", profile.getProfileUrl());
    }

    @Override
    protected void registerForKryo(Kryo kryo) {
        kryo.register(BitbucketProfile.class);
    }

}
