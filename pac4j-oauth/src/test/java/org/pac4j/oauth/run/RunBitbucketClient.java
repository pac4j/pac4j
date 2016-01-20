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
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RunClient;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.BitbucketClient;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link BitbucketClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunBitbucketClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunBitbucketClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup78";
    }

    @Override
    protected IndirectClient getClient() {
        BitbucketClient client = new BitbucketClient();
        client.setKey("bjEt8BMpLwFDqZUvp6");
        client.setSecret("NN6fVXRTcV2qYVejVLZqxBRqHgn3ygD4");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void registerForKryo(Kryo kryo) {
        kryo.register(BitbucketProfile.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        BitbucketProfile profile = (BitbucketProfile) userProfile;
        assertEquals("testscribeup", profile.getUsername());
        assertEquals("Test", profile.getFirstName());
        assertEquals("Scribeup", profile.getFamilyName());
        assertEquals("Test Scribeup", profile.getDisplayName());
        assertFalse(profile.isTeam());
        assertTrue(profile.getPictureUrl().startsWith("https://bitbucket.org/account/testscribeup/avatar"));
        assertEquals("/1.0/users/testscribeup", profile.getProfileUrl());
    }
}
