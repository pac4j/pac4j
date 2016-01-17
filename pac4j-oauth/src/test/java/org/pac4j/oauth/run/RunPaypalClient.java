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
import org.pac4j.core.client.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.client.PayPalClient;
import org.pac4j.oauth.profile.paypal.PayPalAddress;
import org.pac4j.oauth.profile.paypal.PayPalProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link PayPalClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunPaypalClient extends RunClient {

    public static void main(String[] args) throws Exception {
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
        final PayPalClient payPalClient = new PayPalClient(
                "ARQFlBAOdRsb1NhZlutHT_PORP2F-TQpU-Laz-osaBwAHUIBIdg-C8DEsTWY",
                "EAMZPBBfYJGeCBHYkm30xqC-VZ1kePnWZzPLdXyzY43rh-q0OQUH5eucXI6R");
        payPalClient.setCallbackUrl(PAC4J_BASE_URL);
        return payPalClient;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(PayPalProfile.class);
        kryo.register(PayPalAddress.class);
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final PayPalProfile profile = (PayPalProfile) userProfile;
        assertEquals("YAxf5WKSFn4BG_l3wqcBJUSObQTG1Aww5FY0EDf_ccw", profile.getId());
        assertEquals(PayPalProfile.class.getSimpleName() + UserProfile.SEPARATOR
                + "YAxf5WKSFn4BG_l3wqcBJUSObQTG1Aww5FY0EDf_ccw", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), PayPalProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Test", "ScribeUP", "Test ScribeUP", null,
                Gender.UNSPECIFIED, Locale.FRANCE, null, null, "Europe/Berlin");
        final PayPalAddress address = profile.getAddress();
        assertEquals("FR", address.getCountry());
        assertEquals("Paris", address.getLocality());
        assertEquals("75001", address.getPostalCode());
        assertEquals("Adr1", address.getStreetAddress());
        final Locale language = profile.getLanguage();
        assertEquals(Locale.FRANCE, language);
        assertEquals(9, profile.getAttributes().size());
    }
}
