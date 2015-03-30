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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.profile.XmlList;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Company;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Date;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Location;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Position;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
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
public class LinkedIn2ClientIT extends OAuthClientIT {
    
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
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getFormByName("oauth2SAuthorizeForm");
        final HtmlTextInput email = form.getInputByName("session_key");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput password = form.getInputByName("session_password");
        password.setValueAttribute("testpwdscribeup56");
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
        kryo.register(XmlList.class);
        kryo.register(LinkedIn2Position.class);
        kryo.register(LinkedIn2Date.class);
        kryo.register(LinkedIn2Company.class);
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
        assertCommonProfile(userProfile,
                            "testscribeup@gmail.com",
                            "test",
                            "scribeUp",
                            "test scribeUp",
                            null,
                            Gender.UNSPECIFIED,
                            null,
                            "http://m.c.lnkd.licdn.com/mpr/mprx/0_XGm9Ldp1WfMsB74Zk32WLwptW7DZvoWZQisWLwSfnuJeEmY4eXYVwIJ3bFSb9DeNL3uHo21cF5lC",
                            "http://www.linkedin.com/pub/test-scribeup/48/aa/16b", "Paris Area, France");
        final LinkedIn2Location location = profile.getCompleteLocation();
        assertEquals("Paris Area, France", location.getName());
        assertEquals("fr", location.getCode());
        assertNull(profile.getMaidenName());
        assertEquals("ScribeUP développeur chez OpenSource", profile.getHeadline());
        assertEquals("Information Technology and Services", profile.getIndustry());
        assertEquals(1, profile.getNumConnections().intValue());
        assertEquals("This is a summary...", profile.getSummary());
        assertNull(profile.getSpecialties());
        final List<LinkedIn2Position> positions = profile.getPositions();
        assertEquals(2, positions.size());
        final LinkedIn2Position position = positions.get(0);
        assertEquals("417494299", position.getId());
        assertEquals("Developer", position.getTitle());
        assertEquals("Desc", position.getSummary());
        final LinkedIn2Date startDate = position.getStartDate();
        assertEquals(2012, startDate.getYear().intValue());
        assertEquals(3, startDate.getMonth().intValue());
        assertTrue(position.getIsCurrent().booleanValue());
        assertNull(position.getEndDate());
        final LinkedIn2Company company = position.getCompany();
        assertEquals("PAC4J", company.getName());
        assertNull(company.getIndustry());
        assertEquals("http://www.linkedin.com/profile/view?id=167439971&amp;authType=name&amp;authToken=_IWF&amp;trk=api*a167383*s175634*",
                     profile.getSiteStandardProfileRequest());
        assertEquals("167439971", profile.getOAuth10Id());
        assertEquals(14, profile.getAttributes().size());
    }
}
