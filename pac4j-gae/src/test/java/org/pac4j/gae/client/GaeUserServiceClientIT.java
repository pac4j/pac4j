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
package org.pac4j.gae.client;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.client.ClientIT;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.gae.profile.GaeUserServiceProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

/**
 * This class tests the {@link GaeUserServiceProfile} class by simulating a complete authentication.
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
@SuppressWarnings("rawtypes")
public class GaeUserServiceClientIT extends ClientIT implements TestsConstants {
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalUserServiceTestConfig())
                    .setEnvIsAdmin(true).setEnvIsLoggedIn(true).setEnvEmail("test@example.com").setEnvAuthDomain("");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        helper.tearDown();
    }

    @Override
    protected Client getClient() {
        final GaeUserServiceClient client = new GaeUserServiceClient() {
            @Override
            protected RedirectAction retrieveRedirectAction(WebContext context) {
                return RedirectAction.redirect(getCallbackUrl());
            }

        };
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
//        final HtmlForm form = authorizationPage.getForms().get(0);
//        final HtmlTextInput email = form.getInputByName("email");
//        email.setValueAttribute("test@example.com");
//        final HtmlCheckBoxInput isAdmin = form.getInputByName("isAdmin");
//        isAdmin.setChecked(true);
//        List<HtmlInput> submitsInput = form.getInputsByName("action");
//		final HtmlSubmitInput submit = (HtmlSubmitInput) submitsInput.get(0);
//
//        final HtmlPage callbackPage = submit.click();
//        final String callbackUrl = callbackPage.getUrl().toString();
//        logger.debug("callbackUrl : {}", callbackUrl);
        return PAC4J_BASE_URL;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(GaeUserServiceProfile.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final GaeUserServiceProfile profile = (GaeUserServiceProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        final String id = "test@example.com";
        assertEquals(id, profile.getId());
        assertEquals(GaeUserServiceProfile.class.getName() + UserProfile.SEPARATOR
                + id, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GaeUserServiceProfile.class));
        assertCommonProfile(userProfile, id, null, null, "test", null,
                Gender.UNSPECIFIED, null, null, null, null);
        assertEquals(2, profile.getAttributes().size());
    }
}
