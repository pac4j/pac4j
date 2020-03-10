package org.pac4j.openid.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.openid.client.YahooOpenIdClient;
import org.pac4j.openid.profile.yahoo.YahooOpenIdProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link YahooOpenIdClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunYahooOpenIdClient  extends RunClient {

    public static void main(final String[] args) {
        new RunYahooOpenIdClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@yahoo.fr";
    }

    @Override
    protected String getPassword() {
        return "testpwdpac4j";
    }

    @Override
    protected IndirectClient getClient() {
        final YahooOpenIdClient client = new YahooOpenIdClient();
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final YahooOpenIdProfile profile = (YahooOpenIdProfile) userProfile;
        assertNotNull(profile);
        final String id = "mnsYAxIag.AfFGVrKZckRIVkvVYLEYRM4Q--#02050";
        assertEquals("https://me.yahoo.com/a/" + id, profile.getId());
        assertEquals(YahooOpenIdProfile.class.getName() + CommonProfile.SEPARATOR
                + "https://me.yahoo.com/a/" + id, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), YahooOpenIdProfile.class));
        assertCommonProfile(userProfile, getLogin(), null, null, "Test ScribeUP", null,
                Gender.UNSPECIFIED, Locale.FRANCE, "https://s.yimg.com/dg/users/1DJGkdA6uAAECQWEo8AceAQ==.medium.png", null, null);
        assertEquals(4, profile.getAttributes().size());
    }
}
