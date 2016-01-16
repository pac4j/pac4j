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

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.linkedin2.*;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link LinkedIn2Client}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunLinkedIn2Client extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunLinkedIn2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup56";
    }

    @Override
    protected IndirectClient getClient() {
        final LinkedIn2Client client = new LinkedIn2Client();
        client.setKey("gsqj8dn56ayn");
        client.setSecret("kUFAZ2oYvwMQ6HFl");
        client.setScope("r_basicprofile r_emailaddress rw_company_admin w_share");
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(LinkedIn2Profile.class);
        kryo.register(LinkedIn2Location.class);
        kryo.register(LinkedIn2Position.class);
        kryo.register(LinkedIn2Date.class);
        kryo.register(LinkedIn2Company.class);
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final LinkedIn2Profile profile = (LinkedIn2Profile) userProfile;
        logger.debug("profile : {}", profile);
        assertEquals("JJjS_5BOzW", profile.getId());
        assertEquals(LinkedIn2Profile.class.getSimpleName() + UserProfile.SEPARATOR + "JJjS_5BOzW",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedIn2Profile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile,
                null,
                "test",
                "scribeUp",
                null,
                null,
                Gender.UNSPECIFIED,
                null,
                null,
                null,
                null);
        assertEquals("https://www.linkedin.com/profile/view?id=167439971&authType=name&authToken=_IWF&trk=api*a167383*s175634*", profile.getSiteStandardProfileRequest());
        assertEquals("167439971", profile.getOAuth10Id());
        assertEquals(5, profile.getAttributes().size());
    }
}
