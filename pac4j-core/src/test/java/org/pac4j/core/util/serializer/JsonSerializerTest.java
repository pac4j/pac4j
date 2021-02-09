package org.pac4j.core.util.serializer;

import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Serializer}.
 *
 * @author Jerome LELEU
 * @since 3.9.0
 */
public class JsonSerializerTest implements TestsConstants {

    @Test
    public void testString() {
        final var profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);

        final var serializer = new JsonSerializer(CommonProfile.class);
        final var encoded = serializer.serializeToString(profile);
        final var decoded = (CommonProfile) serializer.deserializeFromString(encoded);

        assertEquals(decoded.getId(), profile.getId());
        assertEquals(1, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
    }

    @Test
    public void testBytes() {
        final var profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);

        final var serializer = new JsonSerializer(CommonProfile.class);
        final var encoded = serializer.serializeToBytes(profile);
        final var decoded = (CommonProfile) serializer.deserializeFromBytes(encoded);

        assertEquals(decoded.getId(), profile.getId());
        assertEquals(1, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
    }
}
