/*
  Copyright 2012 - 2014 Jerome Leleu

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.kryo.ColorSerializer;
import org.pac4j.core.kryo.FormattedDateSerializer;
import org.pac4j.core.kryo.LocaleSerializer;
import org.pac4j.core.profile.Color;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.FormattedDate;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class is the generic test case for client.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class TestClient extends TestCase implements TestsConstants {

    protected static final Logger logger = LoggerFactory.getLogger(TestClient.class);

    protected abstract Protocol getProtocol();

    public void testProtocol() {
        final BaseClient client = (BaseClient) getClient();
        assertEquals(getProtocol(), client.getProtocol());
    }

    public void testMissingCallbackUrl() {
        final BaseClient client = (BaseClient) getClient();
        client.setCallbackUrl(null);
        TestsHelper.initShouldFail(client, "callbackUrl cannot be blank");
    }

    protected BaseClient internalTestClone(final BaseClient oldClient) {
        oldClient.setCallbackUrl(CALLBACK_URL);
        final BaseClient client = oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), client.getCallbackUrl());
        assertEquals(oldClient.getName(), client.getName());
        return client;
    }

    public void testAuthenticationAndUserProfileRetrieval() throws Exception {
        ProfileHelper.setKeepRawData(true);
        try {
            final Client client = getClient();

            final MockWebContext context = MockWebContext.create();
            final WebClient webClient = TestsHelper.newWebClient(isJavascriptEnabled());

            final HtmlPage redirectionPage = getRedirectionPage(webClient, client, context);

            updateContextForAuthn(webClient, redirectionPage, context);

            final UserProfile profile = getCredentialsAndProfile(client, context);

            verifyProfile(profile);

            // Java serialization
            byte[] bytes = TestsHelper.serialize(profile);
            final UserProfile profile2 = (UserProfile) TestsHelper.unserialize(bytes);
            verifyProfile(profile2);

            // like CAS serialization
            final Map<String, Object> attributes = profile2.getAttributes();
            final Map<String, Object> newAttributes = new HashMap<String, Object>();
            for (final String key : attributes.keySet()) {
                newAttributes.put(key, attributes.get(key).toString());
            }
            final UserProfile profile3 = ProfileHelper.buildProfile(profile2.getTypedId(), newAttributes);
            verifyProfile(profile3);

            // Kryo serialization
            final Kryo kryo = new Kryo();
            kryo.register(HashMap.class);
            kryo.register(Locale.class, new LocaleSerializer());
            kryo.register(Date.class);
            kryo.register(FormattedDate.class, new FormattedDateSerializer());
            kryo.register(Gender.class);
            kryo.register(Color.class, new ColorSerializer());
            kryo.register(ArrayList.class);
            registerForKryo(kryo);
            bytes = TestsHelper.serializeKryo(kryo, profile);
            final UserProfile profile4 = (UserProfile) TestsHelper.unserializeKryo(kryo, bytes);
            verifyProfile(profile4);
        } finally {
            ProfileHelper.setKeepRawData(false);
        }
    }

    // Default implementation use getCallbackUrl method
    protected void updateContextForAuthn(WebClient webClient, HtmlPage redirectionPage, MockWebContext context)
            throws Exception {
        final String callbackUrl = getCallbackUrl(webClient, redirectionPage);
        final MockWebContext mockWebContext = context;
        mockWebContext.addRequestParameters(TestsHelper.getParametersFromUrl(callbackUrl));
    }

    protected void registerForKryo(final Kryo kryo) {
    }

    protected boolean isJavascriptEnabled() {
        return false;
    }

    protected abstract Client getClient();

    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client client, final MockWebContext context)
            throws Exception {
        final BaseClient baseClient = (BaseClient) client;
        // force immediate redirection for tests
        baseClient.redirect(context, true, false);
        final String redirectionUrl = context.getResponseLocation();
        logger.debug("redirectionUrl : {}", redirectionUrl);
        final HtmlPage loginPage = webClient.getPage(redirectionUrl);
        return loginPage;
    }

    protected abstract String getCallbackUrl(final WebClient webClient, HtmlPage authorizationPage) throws Exception;

    protected UserProfile getCredentialsAndProfile(final Client client, final WebContext context) throws Exception {

        final Credentials credentials = client.getCredentials(context);
        logger.debug("credentials : {}", credentials);

        final UserProfile profile = client.getUserProfile(credentials, context);
        return profile;
    }

    protected abstract void verifyProfile(UserProfile userProfile);

    protected void assertCommonProfile(final UserProfile userProfile, final String email, final String firstName,
            final String familyName, final String displayName, final String username, final Gender gender,
            final Locale locale, final String pictureUrl, final String profileUrl, final String location) {
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
            assertTrue(profile.getPictureUrl().contains(pictureUrl));
        }
        if (profileUrl == null) {
            assertNull(profile.getProfileUrl());
        } else {
            final String profUrl = profile.getProfileUrl();
            assertTrue(profUrl.startsWith(profileUrl));
        }
        assertEquals(location, profile.getLocation());
    }

    protected boolean isCancellable() {
        return false;
    }

    public void testCancelAuthentication() throws Exception {
        if (isCancellable()) {
            final Client client = getClient();

            final MockWebContext context = MockWebContext.create();
            final WebClient webClient = TestsHelper.newWebClient(isJavascriptEnabled());

            final HtmlPage redirectionPage = getRedirectionPage(webClient, client, context);

            updateContextForCancel(redirectionPage, context);

            final UserProfile profile = getCredentialsAndProfile(client, context);

            assertNull(profile);
        }
    }

    protected void updateContextForCancel(HtmlPage redirectionPage, WebContext context) throws Exception {
        final String callbackUrl = getCallbackUrlForCancel(redirectionPage);
        final MockWebContext mockWebContext = (MockWebContext) context;
        mockWebContext.addRequestParameters(TestsHelper.getParametersFromUrl(callbackUrl));
    }

    protected String getCallbackUrlForCancel(final HtmlPage authorizationPage) throws Exception {
        throw new IllegalArgumentException("To be implemented");
    }
}
