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
import org.pac4j.core.client.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link DropBoxClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunDropboxClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunDropboxClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected IndirectClient getClient() {
        final DropBoxClient dropBoxClient = new DropBoxClient();
        dropBoxClient.setKey("0194c6m79qll0ia");
        dropBoxClient.setSecret("a0ylze9a0bhsvxv");
        dropBoxClient.setCallbackUrl(GOOGLE_URL);
        return dropBoxClient;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(DropBoxProfile.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final DropBoxProfile profile = (DropBoxProfile) userProfile;
        assertEquals("75206624", profile.getId());
        assertEquals(DropBoxProfile.class.getSimpleName() + UserProfile.SEPARATOR + "75206624", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), DropBoxProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, null, null, "Test ScribeUP", null, Gender.UNSPECIFIED, Locale.FRENCH,
                null, "https://db.tt/RvmZyvJa", null);
        assertEquals(0L, profile.getShared().longValue());
        assertEquals(1410412L, profile.getNormal().longValue());
        assertEquals(2147483648L, profile.getQuota().longValue());
        assertNotNull(profile.getAccessSecret());
        assertEquals(8, profile.getAttributes().size());
    }
}
