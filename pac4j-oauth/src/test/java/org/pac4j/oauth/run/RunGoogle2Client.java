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
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.profile.google2.Google2Email;
import org.pac4j.oauth.profile.google2.Google2Profile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link Google2Client}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunGoogle2Client extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunGoogle2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup91";
    }

    @Override
    protected IndirectClient getClient() {
        final Google2Client google2Client = new Google2Client();
        google2Client.setKey("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        google2Client.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        google2Client.setCallbackUrl(PAC4J_BASE_URL);
        google2Client.setScope(Google2Client.Google2Scope.EMAIL_AND_PROFILE);
        return google2Client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(Google2Profile.class);
        kryo.register(Google2Email.class);
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final Google2Profile profile = (Google2Profile) userProfile;
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(Google2Profile.class.getSimpleName() + UserProfile.SEPARATOR + "113675986756217860428",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), Google2Profile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
                Gender.MALE, Locale.ENGLISH,
                "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
                "https://plus.google.com/113675986756217860428", null);
        assertNull(profile.getBirthday());
        assertTrue(profile.getEmails() != null && profile.getEmails().size() == 1);
        assertEquals(9, profile.getAttributes().size());
    }
}
