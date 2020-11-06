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
    public void test() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);

        final JsonSerializer serializer = new JsonSerializer(CommonProfile.class);
        final String encoded = serializer.encode(profile);
        final CommonProfile decoded = (CommonProfile) serializer.decode(encoded);

        assertEquals(decoded.getId(), profile.getId());
        assertEquals(1, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
    }
}
