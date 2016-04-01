package org.pac4j.core.util;

import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link KryoSerializationHelper}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class KryoSerializationHelperTests implements TestsConstants {

    private KryoSerializationHelper helper = new KryoSerializationHelper();

    private CommonProfile getUserProfile() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        return profile;
    }

    @Test
    public void testBytesSerialization() {
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = helper.serializeToBytes(profile);
        final CommonProfile profile2 = (CommonProfile) helper.unserializeFromBytes(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testBase64StringSerialization() {
        final CommonProfile profile = getUserProfile();
        final String serialized = helper.serializeToBase64(profile);
        final CommonProfile profile2 = (CommonProfile) helper.unserializeFromBase64(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }
}
