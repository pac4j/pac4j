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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.scribe.up.profile.Gender;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.yahoo.YahooAddress;
import org.scribe.up.profile.yahoo.YahooDisclosure;
import org.scribe.up.profile.yahoo.YahooEmail;
import org.scribe.up.profile.yahoo.YahooImage;
import org.scribe.up.profile.yahoo.YahooInterest;
import org.scribe.up.profile.yahoo.YahooProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.YahooProvider;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.YahooProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestYahooProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        YahooProvider yahooProvider = new YahooProvider();
        yahooProvider
            .setKey("dj0yJmk9QUlLcTVINlBpdm5VJmQ9WVdrOVUxaE5Za3R0TmpJbWNHbzlOVEUyTmpFME1EWXkmcz1jb25zdW1lcnNlY3JldCZ4PTJm");
        yahooProvider.setSecret("95220809156c027c0a10c959a04b099da5510b66");
        yahooProvider.setCallbackUrl("http://www.google.com/");
        yahooProvider.init();
        return yahooProvider;
    }
    
    @Override
    protected String getCallbackUrl(HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getFormByName("login_form");
        HtmlTextInput login = form.getInputByName("login");
        login.setValueAttribute("testscribeup@yahoo.fr");
        HtmlPasswordInput passwd = form.getInputByName("passwd");
        passwd.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName(".save");
        HtmlPage confirmPage = submit.click();
        form = confirmPage.getFormByName("rcForm");
        submit = form.getInputByName("agree");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(UserProfile userProfile) {
        YahooProfile profile = (YahooProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("PCSXZCYSWC6XUJNMZKRGWVPHNU", profile.getId());
        assertEquals(YahooProfile.class.getSimpleName() + UserProfile.SEPARATOR + "PCSXZCYSWC6XUJNMZKRGWVPHNU",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), YahooProfile.class));
        assertEquals("my profile", profile.getAboutMe());
        List<YahooAddress> addresses = profile.getAddresses();
        assertEquals(2, addresses.size());
        YahooAddress address = addresses.get(0);
        assertEquals(3, address.getId());
        assertTrue(address.isCurrent());
        assertEquals(Locale.FRENCH, address.getCountry());
        assertEquals("", address.getState());
        assertEquals("", address.getCity());
        assertEquals("78400", address.getPostalCode());
        assertEquals("", address.getStreet());
        assertEquals("HOME", address.getType());
        assertEquals(1976, profile.getBirthYear());
        assertEquals("03/10", profile.getBirthdate().toString());
        assertEquals("2012-02-06T12:46:43Z", profile.getCreated().toString());
        assertEquals(36, profile.getDisplayAge());
        List<YahooDisclosure> disclosures = profile.getDisclosures();
        assertEquals(2, disclosures.size());
        YahooDisclosure disclosure = disclosures.get(0);
        assertEquals("1", disclosure.getAcceptance());
        assertEquals("bd", disclosure.getName());
        assertTrue(disclosure.getSeen() instanceof Date);
        assertEquals("1", disclosure.getVersion());
        List<YahooEmail> emails = profile.getEmails();
        assertEquals(2, emails.size());
        YahooEmail email = emails.get(1);
        assertEquals(2, email.getId());
        assertTrue(email.isPrimary());
        assertEquals("testscribeup@yahoo.fr", email.getHandle());
        assertEquals("HOME", email.getType());
        assertEquals("ScribeUP", profile.getFamilyName());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals("Test", profile.getGivenName());
        YahooImage image = profile.getImage();
        assertEquals("http://avatars.zenfs.com/users/1DJGkdA6uAAECQWEo8AceAQ==.large.png", image.getImageUrl());
        assertEquals(150, image.getWidth());
        assertEquals(225, image.getHeight());
        assertEquals("150x225", image.getSize());
        List<YahooInterest> interests = profile.getInterests();
        assertEquals(11, interests.size());
        YahooInterest interest = interests.get(0);
        assertEquals("basic interest", interest.getDeclaredInterests().get(0));
        assertEquals("prfFavHobbies", interest.getInterestCategory());
        assertTrue(profile.isConnected());
        assertTrue(profile.isConnectedDefined());
        assertEquals(Locale.FRANCE, profile.getLang());
        assertEquals("Chatou, Ile-de-France", profile.getLocation());
        assertEquals("2012-02-06T12:46:36Z", profile.getMemberSince().toString());
        assertEquals("Test", profile.getNickname());
        assertEquals("http://profile.yahoo.com/PCSXZCYSWC6XUJNMZKRGWVPHNU", profile.getProfileUrl());
        assertEquals("Europe/Paris", profile.getTimeZone());
        assertEquals("2012-03-05T14:26:25Z", profile.getUpdated().toString());
        assertEquals("http://social.yahooapis.com/v1/user/PCSXZCYSWC6XUJNMZKRGWVPHNU/profile", profile.getUri());
        assertEquals(22, profile.getAttributes().size());
    }
}
