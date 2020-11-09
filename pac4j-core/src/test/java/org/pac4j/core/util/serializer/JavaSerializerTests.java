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
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        return profile;
    }

    @Test
    public void testBytesSerialization() {
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = helper.encodeToBytes(profile);
        final CommonProfile profile2 = (CommonProfile) helper.decodeFromBytes(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testBytesSerializationUnsecure() {
        JavaSerializer h = new JavaSerializer();
        h.clearTrustedClasses();
        h.clearTrustedPackages();
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = h.encodeToBytes(profile);
        assertNull(h.decodeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationTrustedClass() {
        JavaSerializer h = new JavaSerializer();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedClass(SimplePrincipalCollection.class);
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.encodeToBytes(spc);
        assertEquals(spc, h.decodeFromBytes(serialized));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTrustedPackagesModification() {
        JavaSerializer h = new JavaSerializer();
        h.getTrustedPackages().add("org.apache");
    }

    @Test
    public void testBytesSerializationTrustedPackage() {
        JavaSerializer h = new JavaSerializer();
        h.addTrustedPackage("org.apache");
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.encodeToBytes(spc);
        assertNotNull(h.decodeFromBytes(serialized));
    }

    @Test
    public void testBase64StringSerialization() {
        final CommonProfile profile = getUserProfile();
        final String serialized = helper.encode(profile);
        final CommonProfile profile2 = (CommonProfile) helper.decode(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testFoundActionSerialization() {
        final FoundAction action = new FoundAction(PAC4J_BASE_URL);
        final byte[] serialized = helper.encodeToBytes(action);
        final FoundAction action2 = (FoundAction) helper.decodeFromBytes(serialized);
        assertEquals(action.getLocation(), action2.getLocation());
    }
}
