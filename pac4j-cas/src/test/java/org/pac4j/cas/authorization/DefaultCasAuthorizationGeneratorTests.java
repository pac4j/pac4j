package org.pac4j.cas.authorization;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DefaultCasAuthorizationGenerator}.
 * 
 * @author Michael Remond
 * @since 1.5.1
 */
public final class DefaultCasAuthorizationGeneratorTests implements TestsConstants {

    @Test
    public void testNoAttribute() {
        final AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        final CasProfile profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testBadAttributeValue() {
        final AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "yes");
        final CasProfile profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsNotRemembered() {
        final AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "false");
        final CasProfile profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsRemembered() {
        final AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "true");
        final CasProfile profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, profile);
        assertEquals(true, profile.isRemembered());
    }
}
