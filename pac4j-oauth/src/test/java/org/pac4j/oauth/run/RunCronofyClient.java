package org.pac4j.oauth.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oauth.client.CronofyClient;
import org.pac4j.oauth.profile.cronofy.CronofyProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Manually run a test for the {@link CronofyClient}.
 *
 * @author Jerome Leleu
 * @since 5.3.1
 */
public final class RunCronofyClient extends RunClient {

    public static void main(String[] args) {
        new RunCronofyClient().run();
    }

    @Override
    protected String getLogin() {
        return null;
    }

    @Override
    protected String getPassword() {
        return null;
    }

    @Override
    protected IndirectClient getClient() {
        val cronofyClient = new CronofyClient();
        cronofyClient.setKey("EYRQwgxE3u9gthGZgI2KbFw5Jrkt5dgt");
        cronofyClient.setSecret("CRN_ovKtaeBlrO0DxBosm9tnXn9l6xHF2HAD0iOeer");
        cronofyClient.setCallbackUrl(PAC4J_BASE_URL);
        cronofyClient.setScope("read_events");
        cronofyClient.setSdkIdentifier("uk");
        return cronofyClient;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        val profile = (CronofyProfile) userProfile;
        assertNotNull(profile.getId());
        assertNotNull(profile.getAccessToken());
        assertNotNull(profile.getRefreshToken());
        assertEquals(7, profile.getAttributes().size());
    }
}
