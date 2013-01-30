/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.core.client;

import java.util.Locale;

import junit.framework.TestCase;

import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class is the generic test case for client.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings({
    "rawtypes", "unchecked"
})
public abstract class TestClient extends TestCase implements TestsConstants {
    
    protected static final Logger logger = LoggerFactory.getLogger(TestClient.class);
    
    public void testMissingCallbackUrl() {
        final BaseClient client = (BaseClient) getClient();
        client.setCallbackUrl(null);
        TestsHelper.initShouldFail(client, "callbackUrl cannot be blank");
    }
    
    protected BaseClient internalTestClone(final BaseClient oldClient) {
        oldClient.setCallbackUrl(CALLBACK_URL);
        oldClient.setFailureUrl(FAILURE_URL);
        final BaseClient client = oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), client.getCallbackUrl());
        assertEquals(oldClient.getFailureUrl(), client.getFailureUrl());
        assertEquals(oldClient.getName(), client.getName());
        return client;
    }
    
    public void testAuthenticationAndUserProfileRetrieval() throws Exception {
        final Client client = getClient();
        
        final MockWebContext context = MockWebContext.create();
        final WebClient webClient = TestsHelper.newWebClient(isJavascriptEnabled());
        
        final HtmlPage redirectionPage = getRedirectionPage(webClient, client, context);
        
        final String callbackUrl = getCallbackUrl(redirectionPage);
        
        final UserProfile profile = getProfile(client, context, callbackUrl);
        
        verifyProfile(profile);
        
        final byte[] bytes = TestsHelper.serialize(profile);
        final UserProfile profile2 = (UserProfile) TestsHelper.unserialize(bytes);
        
        verifyProfile(profile2);
    }
    
    protected boolean isJavascriptEnabled() {
        return false;
    }
    
    protected abstract Client getClient();
    
    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client client, final WebContext context)
        throws Exception {
        final String redirectionUrl = client.getRedirectionUrl(context);
        logger.debug("redirectionUrl : {}", redirectionUrl);
        final HtmlPage loginPage = webClient.getPage(redirectionUrl);
        return loginPage;
    }
    
    protected abstract String getCallbackUrl(HtmlPage authorizationPage) throws Exception;
    
    protected UserProfile getProfile(final Client client, final WebContext context, final String callbackUrl)
        throws Exception {
        
        final MockWebContext mockWebContext = (MockWebContext) context;
        mockWebContext.addRequestParameters(TestsHelper.getParametersFromUrl(callbackUrl));
        final Credentials credentials = client.getCredentials(context);
        logger.debug("credentials : {}", credentials);
        
        final UserProfile profile = client.getUserProfile(credentials);
        return profile;
    }
    
    protected abstract void verifyProfile(UserProfile userProfile);
    
    protected void assertCommonProfile(final UserProfile userProfile, final String email, final String firstName,
                                       final String familyName, final String displayName, final String username,
                                       final Gender gender, final Locale locale, final String pictureUrl,
                                       final String profileUrl, final String location) {
        final CommonProfile profile = (CommonProfile) userProfile;
        assertEquals(email, profile.getEmail());
        assertEquals(firstName, profile.getFirstName());
        assertEquals(familyName, profile.getFamilyName());
        assertEquals(displayName, profile.getDisplayName());
        assertEquals(username, profile.getUsername());
        assertEquals(gender, profile.getGender());
        assertEquals(locale, profile.getLocale());
        if (pictureUrl == null) {
            assertNull(profile.getPictureUrl());
        } else {
            assertTrue(profile.getPictureUrl().startsWith(pictureUrl));
        }
        if (profileUrl == null) {
            assertNull(profile.getProfileUrl());
        } else {
            assertTrue(profile.getProfileUrl().startsWith(profileUrl));
        }
        assertEquals(location, profile.getLocation());
    }
}
