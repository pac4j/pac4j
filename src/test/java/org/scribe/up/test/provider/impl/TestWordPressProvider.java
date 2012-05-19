/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.provider.impl;

import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.wordpress.WordPressLinks;
import org.scribe.up.profile.wordpress.WordPressProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.WordPressProvider;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.WordPressProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TestWordPressProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        WordPressProvider wordPressProvider = new WordPressProvider();
        wordPressProvider.setKey("209");
        wordPressProvider.setSecret("xJBXMRVvKrvHqyvM6BpzkenJVMIdQrIWKjPJsezjGYu71y7sDgt8ibz6s9IFLqU8");
        wordPressProvider.setCallbackUrl("http://www.google.com/");
        wordPressProvider.init();
        return wordPressProvider;
    }
    
    @Override
    protected String getCallbackUrl(HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getFormByName("loginform");
        HtmlTextInput login = form.getInputByName("log");
        login.setValueAttribute("testscribeup");
        HtmlPasswordInput passwd = form.getInputByName("pwd");
        passwd.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName("wp-submit");
        HtmlPage confirmPage = submit.click();
        form = confirmPage.getFormByName("loginform");
        submit = form.getInputByName("wp-submit");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(UserProfile userProfile) {
        WordPressProfile profile = (WordPressProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("35944437", profile.getId());
        assertEquals(WordPressProfile.class.getSimpleName() + UserProfile.SEPARATOR + "35944437", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), WordPressProfile.class));
        assertEquals("testscribeup", profile.getDisplayName());
        assertEquals("testscribeup", profile.getUsername());
        assertEquals("testscribeup@gmail.com", profile.getEmail());
        assertEquals(36224958, profile.getPrimaryBlog());
        assertTrue(profile.isPrimaryBlogDefined());
        assertEquals("http://0.gravatar.com/avatar/67c3844a672979889c1e3abbd8c4eb22?s=96&d=identicon&r=G",
                     profile.getAvatarUrl());
        assertEquals("http://en.gravatar.com/testscribeup", profile.getProfileUrl());
        WordPressLinks links = profile.getLinks();
        assertEquals("https://public-api.wordpress.com/rest/v1/me", links.getSelf());
        assertEquals("https://public-api.wordpress.com/rest/v1/me/help", links.getHelp());
        assertEquals("https://public-api.wordpress.com/rest/v1/sites/36224958", links.getSite());
        assertEquals(7, profile.getAttributes().size());
    }
}
