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
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.foursquare.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link FoursquareClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunFoursquare extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunFoursquare().run();
    }

    @Override
    protected String getLogin() {
        return "pac4j@mailinator.com";
    }

    @Override
    protected String getPassword() {
        return "pac4j";
    }

    @Override
    protected IndirectClient getClient() {
        final FoursquareClient foursquareClient = new FoursquareClient();
        foursquareClient.setKey("CONTW2V0SBAHTMXMUA2G1I2P55WGRVJLGBLNY2CFSG1JV4DQ");
        foursquareClient.setSecret("EVAZNDHEQODSIPOKC13JAAPMR3IJRSMLE55TYUW3VYRY3VTC");
        foursquareClient.setCallbackUrl(PAC4J_BASE_URL);
        return foursquareClient;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(FoursquareProfile.class);
        kryo.register(FoursquareUserFriends.class);
        kryo.register(ArrayList.class);
        kryo.register(FoursquareUserFriendGroup.class);
        kryo.register(FoursquareUserFriend.class);
        kryo.register(FoursquareUserContact.class);
        kryo.register(FoursquareUserPhoto.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final FoursquareProfile profile = (FoursquareProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("81827700", profile.getId());
        assertEquals(FoursquareProfile.class.getSimpleName() + UserProfile.SEPARATOR + "81827700", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), FoursquareProfile.class));
        assertCommonProfile(userProfile,
                "pac4j@mailinator.com",
                "Pac4j",
                "Pac4j",
                null,
                null,
                Gender.UNSPECIFIED,
                null,
                "https://irs0.4sqi.net/img/user/original/blank_boy.png",
                "https://foursquare.com/user/81827700", "");
    }
}
