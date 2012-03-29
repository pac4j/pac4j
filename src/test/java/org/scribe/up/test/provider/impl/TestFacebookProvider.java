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
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.facebook.FacebookEducation;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookProfile;
import org.scribe.up.profile.facebook.FacebookRelationshipStatus;
import org.scribe.up.profile.facebook.FacebookWork;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.FacebookProvider;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.FacebookProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestFacebookProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.setKey("291329260930505");
        facebookProvider.setSecret("8ace9cbf90dcecfeb36c285854db55ab");
        facebookProvider.setCallbackUrl("http://www.google.com/");
        facebookProvider
            .setScope("email,user_likes,user_about_me,user_birthday,user_education_history,user_hometown,user_relationship_details,user_location,user_religion_politics,user_relationships,user_work_history,user_website");
        facebookProvider.init();
        return facebookProvider;
    }
    
    @Override
    protected String getCallbackUrl(HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getForms().get(0);
        HtmlTextInput email = form.getInputByName("email");
        email.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput password = form.getInputByName("pass");
        password.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName("login");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(UserProfile userProfile) {
        FacebookProfile profile = (FacebookProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("100003571536393", profile.getId());
        assertEquals("Jerome Testscribeup", profile.getName());
        assertEquals("jerome", profile.getFirstName());
        // middle_name
        assertEquals("Testscribeup", profile.getLastName());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(Locale.FRANCE, profile.getLocale());
        List<FacebookObject> languages = profile.getLanguages();
        assertEquals("Français", languages.get(0).getName());
        assertEquals("http://www.facebook.com/profile.php?id=100003571536393", profile.getLink());
        // username
        // third_party_id
        assertEquals(1, profile.getTimezone());
        assertTrue(profile.getUpdateTime() instanceof Date);
        // verified
        assertEquals("A propos de moi", profile.getBio());
        assertTrue(profile.getBirthday() instanceof Date);
        List<FacebookEducation> educations = profile.getEducation();
        FacebookEducation education = educations.get(0);
        assertEquals("lycée mixte", education.getSchool().getName());
        assertEquals("2000", education.getYear().getName());
        assertEquals("High School", education.getType());
        education = educations.get(1);
        assertEquals("Ingénieur", education.getDegree().getName());
        assertEquals("testscribeup@gmail.com", profile.getEmail());
        assertEquals("San Francisco, California", (profile.getHometown()).getName());
        assertEquals("female", (profile.getInterestedIn()).get(0));
        assertEquals("New York, New York", (profile.getLocation()).getName());
        assertEquals("Sans Opinion (desc)", profile.getPolitical());
        List<FacebookObject> favoriteAthletes = profile.getFavoriteAthletes();
        assertEquals("Surfing", favoriteAthletes.get(0).getName());
        List<FacebookObject> favoriteTeams = profile.getFavoriteTeams();
        assertEquals("Handball Féminin de France", favoriteTeams.get(0).getName());
        assertEquals("citation", profile.getQuotes());
        assertEquals(FacebookRelationshipStatus.MARRIED, profile.getRelationshipStatus());
        assertEquals("Athéisme (desc)", profile.getReligion());
        // significant_other
        assertEquals("web site", profile.getWebsite());
        List<FacebookWork> works = profile.getWork();
        FacebookWork work = works.get(0);
        assertEquals("Employeur", work.getEmployer().getName());
        assertEquals("Paris, France", work.getLocation().getName());
        assertEquals("Architecte Web", work.getPosition().getName());
        assertEquals("Description", work.getDescription());
        assertTrue(work.getStartDate() instanceof Date);
        assertNull(work.getEndDate());
        assertEquals(24, profile.getAttributes().size());
    }
}
