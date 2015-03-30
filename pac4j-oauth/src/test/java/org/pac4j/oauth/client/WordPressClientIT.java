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

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.wordpress.WordPressLinks;
import org.pac4j.oauth.profile.wordpress.WordPressProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link WordPressClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressClientIT extends OAuthClientIT {

    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final WordPressClient wordPressClient = new WordPressClient();
        wordPressClient.setKey("209");
        wordPressClient.setSecret("xJBXMRVvKrvHqyvM6BpzkenJVMIdQrIWKjPJsezjGYu71y7sDgt8ibz6s9IFLqU8");
        wordPressClient.setCallbackUrl(GOOGLE_URL + "/");
        return wordPressClient;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getFormByName("loginform");
        final HtmlTextInput login = form.getInputByName("log");
        login.setValueAttribute("testscribeup");
        final HtmlPasswordInput passwd = form.getInputByName("pwd");
        passwd.setValueAttribute("testpwdscribeup");

        HtmlElement button = (HtmlElement) authorizationPage.createElement("button");
        button.setAttribute("type", "submit");
        form.appendChild(button);
        // HtmlButton button = form.getButtonByName("wp-submit");

        final HtmlPage confirmPage = button.click();
        form = confirmPage.getFormByName("loginform");

        button = (HtmlElement) confirmPage.createElement("button");
        button.setAttribute("type", "submit");
        form.appendChild(button);
        // button = form.getButtonByName("wp-submit");

        final HtmlPage callbackPage = button.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(WordPressProfile.class);
        kryo.register(WordPressLinks.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final WordPressProfile profile = (WordPressProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("35944437", profile.getId());
        assertEquals(WordPressProfile.class.getSimpleName() + UserProfile.SEPARATOR + "35944437", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), WordPressProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", null, null, "testscribeup", "testscribeup",
                Gender.UNSPECIFIED, null,
                "https://0.gravatar.com/avatar/67c3844a672979889c1e3abbd8c4eb22?s=96&d=identicon&r=G",
                "http://en.gravatar.com/testscribeup", null);
        assertEquals(36224958, profile.getPrimaryBlog().intValue());
        final WordPressLinks links = profile.getLinks();
        assertEquals("https://public-api.wordpress.com/rest/v1/me", links.getSelf());
        assertEquals("https://public-api.wordpress.com/rest/v1/me/help", links.getHelp());
        assertEquals("https://public-api.wordpress.com/rest/v1/sites/36224958", links.getSite());
        assertEquals(8, profile.getAttributes().size());
    }
}
