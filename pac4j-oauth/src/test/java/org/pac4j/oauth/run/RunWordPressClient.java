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
package org.pac4j.oauth.run;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.WordPressClient;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;
import org.pac4j.oauth.profile.wordpress.WordPressLinks;
import org.pac4j.oauth.profile.wordpress.WordPressProfile;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link WordPressClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunWordPressClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunWordPressClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected IndirectClient getClient() {
        final WordPressClient wordPressClient = new WordPressClient();
        wordPressClient.setKey("209");
        wordPressClient.setSecret("xJBXMRVvKrvHqyvM6BpzkenJVMIdQrIWKjPJsezjGYu71y7sDgt8ibz6s9IFLqU8");
        wordPressClient.setCallbackUrl(PAC4J_URL);
        return wordPressClient;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(WindowsLiveProfile.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
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
