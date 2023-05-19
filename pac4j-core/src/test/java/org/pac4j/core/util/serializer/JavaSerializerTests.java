package org.pac4j.core.util.serializer;

import lombok.val;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Test;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link JavaSerializer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class JavaSerializerTests implements TestsConstants {

    private JavaSerializer helper = new JavaSerializer();

    private CommonProfile getUserProfile() {
        val profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        return profile;
    }

    @Test
    public void testBytesSerialization() {
        val profile = getUserProfile();
        val serialized = helper.serializeToBytes(profile);
        UserProfile profile2 = (CommonProfile) helper.deserializeFromBytes(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testStringSerialization() {
        val profile = getUserProfile();
        val serialized = helper.serializeToString(profile);
        UserProfile profile2 = (CommonProfile) helper.deserializeFromString(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testBytesSerializationUnsecure() {
        var h = new JavaSerializer();
        h.clearTrustedClasses();
        h.clearTrustedPackages();
        val profile = getUserProfile();
        val serialized = h.serializeToBytes(profile);
        assertNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationTrustedClass() {
        var h = new JavaSerializer();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedClass(SimplePrincipalCollection.class);
        val spc = new SimplePrincipalCollection();
        val serialized = h.serializeToBytes(spc);
        assertEquals(spc, h.deserializeFromBytes(serialized));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTrustedPackagesModification() {
        var h = new JavaSerializer();
        h.getTrustedPackages().add("org.apache");
    }

    @Test
    public void testBytesSerializationTrustedPackage() {
        var h = new JavaSerializer();
        h.addTrustedPackage("org.apache");
        val spc = new SimplePrincipalCollection();
        val serialized = h.serializeToBytes(spc);
        assertNotNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBase64StringSerialization() {
        val profile = getUserProfile();
        val serialized = helper.serializeToString(profile);
        UserProfile profile2 = (CommonProfile) helper.deserializeFromString(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testFoundActionSerialization() {
        WithLocationAction action = new FoundAction(PAC4J_BASE_URL);
        val serialized = helper.serializeToBytes(action);
        WithLocationAction action2 = (FoundAction) helper.deserializeFromBytes(serialized);
        assertEquals(action.getLocation(), action2.getLocation());
    }
}
