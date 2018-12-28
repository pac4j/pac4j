package org.pac4j.core.util;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.example.DummyValue;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serializable;

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
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = h.serializeToBytes(profile);
        assertNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationTrustedClass() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedClass(DummyValue.class);

        Serializable object = new DummyValue("value1");
        final byte[] serialized = h.serializeToBytes(object );
        assertEquals(object , h.deserializeFromBytes(serialized));
    }

    @Test
    public void testTrustedPackageDeserialization() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.clearTrustedPackages();
        h.clearTrustedClasses();
        h.addTrustedPackage("org.example");

        Serializable object = new DummyValue("value1");
        final byte[] serialized = h.serializeToBytes(object );
        assertEquals(object , h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationMadeSecure() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.addTrustedPackage("org.apache");
        final SimplePrincipalCollection spc = new SimplePrincipalCollection();
        final byte[] serialized = h.serializeToBytes(spc);
        assertNotNull(h.deserializeFromBytes(serialized));
    }

    @Test
    public void testBytesSerializationUnsecureNullTrustedPackages() {
        JavaSerializationHelper h = new JavaSerializationHelper();
        h.setTrustedPackages(null);
        final CommonProfile profile = getUserProfile();
        final byte[] serialized = h.serializeToBytes(profile);
        TestsHelper.expectException(() -> h.deserializeFromBytes(serialized), TechnicalException.class, "trustedPackages cannot be null");
    }

    @Test
    public void testBase64StringSerialization() {
        final CommonProfile profile = getUserProfile();
        final String serialized = helper.serializeToBase64(profile);
        final CommonProfile profile2 = (CommonProfile) helper.deserializeFromBase64(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }
}
