package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.FoursquareClient;
import org.pac4j.oauth.profile.foursquare.*;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link FoursquareClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunFoursquareClient extends RunClient {

    public static void main(String[] args) {
        new RunFoursquareClient().run();
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
        final var foursquareClient = new FoursquareClient();
        foursquareClient.setKey("CONTW2V0SBAHTMXMUA2G1I2P55WGRVJLGBLNY2CFSG1JV4DQ");
        foursquareClient.setSecret("EVAZNDHEQODSIPOKC13JAAPMR3IJRSMLE55TYUW3VYRY3VTC");
        foursquareClient.setCallbackUrl(PAC4J_BASE_URL);
        return foursquareClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final var profile = (FoursquareProfile) userProfile;
        assertEquals("81827700", profile.getId());
        assertEquals(FoursquareProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "81827700", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), FoursquareProfile.class));
        assertCommonProfile(userProfile,
                "pac4j@mailinator.com",
                "Pac4j",
                "Pac4j",
                null,
                null,
                Gender.UNSPECIFIED,
                null,
                "https://igx.4sqi.net/img/user/original/blank_boy.png",
                "https://foursquare.com/user/81827700", "");
    }
}
