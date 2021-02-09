package org.pac4j.core.util.serializer;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Test;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
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
        final var profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        return profile;
    }

    @Test
    public void testBytesSerialization() {
        final var profile = getUserProfile();
        final var serialized = helper.serializeToBytes(profile);
        final var profile2 = (CommonProfile) helper.deserializeFromBytes(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testStringSerialization() {
        final var profile = getUserProfile();
        final var serialized = helper.serializeToString(profile);
        final var profile2 = (CommonProfile) helper.deserializeFromString(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testBytesSerializationUnsecure() {
        var h = new JavaSerializer();
        h.clearTrustedClasses();
        h.clearTrustedPackages();
        final var profile = getUserProfile();
        final var serialized = h.serializeToBytes(profile);
        assertNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationTrustedClass() {
        var h = new JavaSerializer();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedClass(SimplePrincipalCollection.class);
        final var spc = new SimplePrincipalCollection();
        final var serialized = h.serializeToBytes(spc);
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
        final var spc = new SimplePrincipalCollection();
        final var serialized = h.serializeToBytes(spc);
        assertNotNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBase64StringSerialization() {
        final var profile = getUserProfile();
        final var serialized = helper.serializeToString(profile);
        final var profile2 = (CommonProfile) helper.deserializeFromString(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testFoundActionSerialization() {
        final var action = new FoundAction(PAC4J_BASE_URL);
        final var serialized = helper.serializeToBytes(action);
        final var action2 = (FoundAction) helper.deserializeFromBytes(serialized);
        assertEquals(action.getLocation(), action2.getLocation());
    }
}
