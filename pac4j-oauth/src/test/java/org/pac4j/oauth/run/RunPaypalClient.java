package org.pac4j.oauth.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.PayPalClient;
import org.pac4j.oauth.profile.paypal.PayPalProfile;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Run manually a test for the {@link PayPalClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunPaypalClient extends RunClient {

    public static void main(String[] args) {
        new RunPaypalClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "a1z2e3r4!$";
    }

    @Override
    protected IndirectClient getClient() {
        val payPalClient = new PayPalClient(
                "ARQFlBAOdRsb1NhZlutHT_PORP2F-TQpU-Laz-osaBwAHUIBIdg-C8DEsTWY",
                "EAMZPBBfYJGeCBHYkm30xqC-VZ1kePnWZzPLdXyzY43rh-q0OQUH5eucXI6R");
        payPalClient.setCallbackUrl(PAC4J_BASE_URL);
        return payPalClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        val profile = (PayPalProfile) userProfile;
        assertEquals("YAxf5WKSFn4BG_l3wqcBJUSObQTG1Aww5FY0EDf_ccw", profile.getId());
        assertEquals(PayPalProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR
                + "YAxf5WKSFn4BG_l3wqcBJUSObQTG1Aww5FY0EDf_ccw", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), PayPalProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Test", "ScribeUP", "Test ScribeUP", null,
                Gender.UNSPECIFIED, Locale.FRANCE, null, null, "Europe/Berlin");
        val address = profile.getAddress();
        assertEquals("FR", address.getCountry());
        assertEquals("Paris", address.getLocality());
        assertEquals("75001", address.getPostalCode());
        assertEquals("Adr1", address.getStreetAddress());
        val language = profile.getLanguage();
        assertEquals(Locale.FRANCE, language);
        assertEquals(9, profile.getAttributes().size());
    }
}
