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
import java.util.Map;

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.facebook.FacebookEducation;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookRelationshipStatus;
import org.scribe.up.profile.facebook.FacebookWork;
import org.scribe.up.provider.impl.FacebookProvider;
import org.scribe.up.test.util.SingleUserSession;
import org.scribe.up.test.util.WebHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the FacebookProvider by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestFacebookProvider.class);
    
    @SuppressWarnings("unchecked")
    public void testProvider() throws Exception {
        // init provider
        FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.setKey("291329260930505");
        facebookProvider.setSecret("8ace9cbf90dcecfeb36c285854db55ab");
        facebookProvider.setCallbackUrl("http://www.google.com/");
        facebookProvider
            .setScope("email,user_likes,user_about_me,user_birthday,user_education_history,user_hometown,user_relationship_details,user_location,user_religion_politics,user_relationships,user_work_history,user_website");
        facebookProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = facebookProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput email = form.getInputByName("email");
        email.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput password = form.getInputByName("pass");
        password.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName("login");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = facebookProvider.getCredential(WebHelper.getParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = facebookProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        UserProfile userProfile = facebookProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", userProfile);
        assertEquals("100003571536393", userProfile.getId());
        Map<String, Object> attributes = userProfile.getAttributes();
        assertEquals(24, attributes.size());
        assertEquals("Jerome Testscribeup", attributes.get("name"));
        assertEquals("jerome", attributes.get("first_name"));
        // middle_name
        assertEquals("Testscribeup", attributes.get("last_name"));
        assertEquals(Gender.MALE, attributes.get("gender"));
        assertEquals(Locale.FRANCE, attributes.get("locale"));
        List<FacebookObject> languages = (List<FacebookObject>) attributes.get("languages");
        assertEquals("Français", languages.get(0).getName());
        assertEquals("http://www.facebook.com/profile.php?id=100003571536393", attributes.get("link"));
        // username
        // third_party_id
        assertEquals(1, attributes.get("timezone"));
        assertTrue(attributes.get("updated_time") instanceof Date);
        // verified
        assertEquals("A propos de moi", attributes.get("bio"));
        assertTrue(attributes.get("birthday") instanceof Date);
        List<FacebookEducation> educations = (List<FacebookEducation>) attributes.get("education");
        FacebookEducation education = educations.get(0);
        assertEquals("lycée mixte", education.getSchool().getName());
        assertEquals("2000", education.getYear().getName());
        assertEquals("High School", education.getType());
        education = educations.get(1);
        assertEquals("Ingénieur", education.getDegree().getName());
        assertEquals("testscribeup@gmail.com", attributes.get("email"));
        assertEquals("San Francisco, California", ((FacebookObject) attributes.get("hometown")).getName());
        assertEquals("female", ((List<String>) attributes.get("interested_in")).get(0));
        assertEquals("New York, New York", ((FacebookObject) attributes.get("location")).getName());
        assertEquals("Sans Opinion (desc)", attributes.get("political"));
        List<FacebookObject> favoriteAthletes = (List<FacebookObject>) attributes.get("favorite_athletes");
        assertEquals("Surfing", favoriteAthletes.get(0).getName());
        List<FacebookObject> favoriteTeams = (List<FacebookObject>) attributes.get("favorite_teams");
        assertEquals("Handball Féminin de France", favoriteTeams.get(0).getName());
        assertEquals("citation", attributes.get("quotes"));
        assertEquals(FacebookRelationshipStatus.MARRIED, attributes.get("relationship_status"));
        assertEquals("Athéisme (desc)", attributes.get("religion"));
        // significant_other
        assertEquals("web site", attributes.get("website"));
        List<FacebookWork> works = (List<FacebookWork>) attributes.get("work");
        FacebookWork work = works.get(0);
        assertEquals("Employeur", work.getEmployer().getName());
        assertEquals("Paris, France", work.getLocation().getName());
        assertEquals("Architecte Web", work.getPosition().getName());
        assertEquals("Description", work.getDescription());
        assertTrue(work.getStartDate() instanceof Date);
        assertNull(work.getEndDate());
    }
}
