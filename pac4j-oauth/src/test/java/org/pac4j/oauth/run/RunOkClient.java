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
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.OkClient;
import org.pac4j.oauth.profile.ok.OkProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link OkClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunOkClient extends RunClient {

    /**
     * Real profile id.
     */
    private static final String TEST_PROFILE_ID = "";
    /**
     * Real profile location.
     */
    private static final String TEST_LOCATION = "";
    /**
     * Real profile locale.
     */
    private static final java.lang.String TEST_LOCALE = "ru";
    /**
     * Real profile first name.
     */
    private static final String TEST_FIRST_NAME = "";
    /**
     * Real profile last name.
     */
    private static final String TEST_LAST_NAME = "";
    /**
     * Real profile picture url.
     */
    private static final String TEST_PROFILE_PICTURE_URL = "";

    public static void main(String[] args) throws Exception {
        new RunOkClient().run();
    }

    @Override
    protected String getLogin() {
        return "";
    }

    @Override
    protected String getPassword() {
        return "";
    }

    @Override
    protected IndirectClient getClient() {
        final OkClient okClient = new OkClient();
        okClient.setKey("1139019264");
        okClient.setPublicKey("CBAPAFOEEBABABABA");
        okClient.setSecret("479452FD7CA726DF558B4303");
        okClient.setCallbackUrl(PAC4J_URL);
        return okClient;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(OkProfile.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final OkProfile profile = (OkProfile) userProfile;
        assertEquals(TEST_PROFILE_ID, profile.getId());
        assertEquals(OkProfile.class.getSimpleName() + UserProfile.SEPARATOR + TEST_PROFILE_ID,
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), OkProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(
                userProfile,
                null,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_FIRST_NAME + " " + TEST_LAST_NAME,
                TEST_PROFILE_ID,
                Gender.MALE,
                new Locale(TEST_LOCALE),
                TEST_PROFILE_PICTURE_URL,
                OkProfile.BASE_PROFILE_URL + TEST_PROFILE_ID,
                TEST_LOCATION);
    }
}
