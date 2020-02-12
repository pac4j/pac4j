package org.pac4j.core.util;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Test;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.*;

/**
 * Tests {@link JavaSerializationHelper}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class JavaSerializationHelperTests implements TestsConstants {

    private JavaSerializationHelper helper = new JavaSerializationHelper();

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
        final CommonProfile profile2 = (CommonProfile) helper.deserializeFromBytes(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testBytesSerializationUnsecure() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.clearTrustedClasses();
        h.clearTrustedPackages();
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = h.serializeToBytes(profile);
        assertNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationTrustedClass() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedClass(SimplePrincipalCollection.class);
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.serializeToBytes(spc);
        assertEquals(spc, h.deserializeFromBytes(serialized));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTrustedPackagesModification() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.getTrustedPackages().add("org.apache");
    }

    @Test
    public void testBytesSerializationTrustedPackage() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.addTrustedPackage("org.apache");
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.serializeToBytes(spc);
        assertNotNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBase64StringSerialization() {
        final CommonProfile profile = getUserProfile();
        final String serialized = helper.serializeToBase64(profile);
        final CommonProfile profile2 = (CommonProfile) helper.deserializeFromBase64(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testFoundActionSerialization() {
        final FoundAction action = new FoundAction(PAC4J_BASE_URL);
        final byte[] serialized = helper.serializeToBytes(action);
        final FoundAction action2 = (FoundAction) helper.deserializeFromBytes(serialized);
        assertEquals(action.getLocation(), action2.getLocation());
    }
}
