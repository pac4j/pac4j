package org.pac4j.core.util.serializer;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;

import java.time.Instant;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Serializer}.
 *
 * @author Jerome LELEU
 * @since 3.9.0
 */
public class JsonSerializerTest implements TestsConstants {

    @Test
    public void testString() {
        val profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);

        val serializer = new JsonSerializer(CommonProfile.class);
        val encoded = serializer.serializeToString(profile);
        UserProfile decoded = (CommonProfile) serializer.deserializeFromString(encoded);

        assertEquals(decoded.getId(), profile.getId());
        assertEquals(1, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
    }

    @Test
    public void testBytes() {
        val profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);

        val serializer = new JsonSerializer(CommonProfile.class);
        val encoded = serializer.serializeToBytes(profile);
        UserProfile decoded = (CommonProfile) serializer.deserializeFromBytes(encoded);

        assertEquals(decoded.getId(), profile.getId());
        assertEquals(1, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
    }

    @Test
    public void testMultipleProfilesString() {
        val profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);
        val profiles = new LinkedHashMap<String, UserProfile>();
        profiles.put("myprofile", profile);

        val serializer = new JsonSerializer();
        val encoded = serializer.serializeToString(profiles);
        val decoded = serializer.deserializeFromString(encoded);

        assertEquals(LinkedHashMap.class, decoded.getClass());
        val profiles2 = (LinkedHashMap<String, UserProfile>) decoded;
        assertEquals(1, profiles2.size());
        assertEquals(profile, profiles2.get("myprofile"));
    }

    @Test
    public void testMultipleProfilesBytes() {
        val profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);
        val profiles = new LinkedHashMap<String, UserProfile>();
        profiles.put("myprofile", profile);

        val serializer = new JsonSerializer();
        val encoded = serializer.serializeToBytes(profiles);
        val decoded = serializer.deserializeFromBytes(encoded);

        assertEquals(LinkedHashMap.class, decoded.getClass());
        val profiles2 = (LinkedHashMap<String, UserProfile>) decoded;
        assertEquals(1, profiles2.size());
        assertEquals(profile, profiles2.get("myprofile"));
    }

    @Test
    public void testCanSerializeProfilesWithTimeValues() {
        val now = Instant.now();
        val profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute("notOnOrAfter", now);
        val profiles = new LinkedHashMap<String, UserProfile>();
        profiles.put("myprofile", profile);

        val serializer = new JsonSerializer();
        val encoded = serializer.serializeToBytes(profiles);
        val decoded = serializer.deserializeFromBytes(encoded);

        assertEquals(LinkedHashMap.class, decoded.getClass());
        val profiles2 = (LinkedHashMap<String, UserProfile>) decoded;
        assertEquals(1, profiles2.size());
        assertEquals(profile, profiles2.get("myprofile"));
    }
}
