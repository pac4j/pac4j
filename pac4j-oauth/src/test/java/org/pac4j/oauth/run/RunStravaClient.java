package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.client.StravaClient;
import org.pac4j.oauth.profile.strava.StravaProfile;

import static org.junit.Assert.assertEquals;

/**
 * Run manually a test for the {@link StravaClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunStravaClient extends RunClient {

    public static void main(final String[] args) {
        new RunStravaClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@yahoo.fr";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected IndirectClient getClient() {
        final StravaClient stravaClient = new StravaClient();
        stravaClient.setApprovalPrompt("force");
        stravaClient.setKey("3945");
        stravaClient.setSecret("f03df80582396cddfbe0b895a726bac27c8cf739");
        stravaClient.setCallbackUrl(PAC4J_BASE_URL);
        stravaClient.setScope("view_private");
        return stravaClient;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final StravaProfile profile = (StravaProfile) userProfile;
        assertEquals("7319316", profile.getId());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(Integer.valueOf(3), profile.getResourceState());
        assertEquals(Boolean.FALSE, profile.isPremium());
        assertEquals("Nord-Pas-de-Calais", profile.getState());
        assertEquals("Pac4j", profile.getFamilyName());
        assertEquals("Adrian", profile.getFirstName());
        assertEquals("feet", profile.getMeasurementPreference());
        assertEquals("avatar/athlete/medium.png", profile.getProfileMedium());
        assertEquals("Hem", profile.getLocation());
        assertEquals("France", profile.getCountry());
        assertEquals("testscribeup@yahoo.fr", profile.getEmail());
        assertEquals("avatar/athlete/large.png", profile.getPictureUrl().toString());

        assertEquals(1, profile.getBikes().size());
        assertEquals("b1700138", profile.getBikes().get(0).getId());
        assertEquals(Boolean.TRUE, profile.getBikes().get(0).getPrimary());
        assertEquals("BH G5", profile.getBikes().get(0).getName());
        assertEquals(Integer.valueOf(2), profile.getBikes().get(0).getResourceState());

        assertEquals(1, profile.getShoes().size());
        assertEquals("g592532", profile.getShoes().get(0).getId());
        assertEquals(Boolean.TRUE, profile.getShoes().get(0).getPrimary());
        assertEquals("adidas Runner little big", profile.getShoes().get(0).getName());
        assertEquals(Integer.valueOf(2), profile.getShoes().get(0).getResourceState());
    }
}
