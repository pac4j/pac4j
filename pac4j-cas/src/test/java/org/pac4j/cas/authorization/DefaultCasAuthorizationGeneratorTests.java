package org.pac4j.cas.authorization;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DefaultCasAuthzGenerator}.
 *
 * @author Michael Remond
 * @since 1.5.1
 */
public final class DefaultCasAuthorizationGeneratorTests implements TestsConstants {

    @Test
    public void testNoAttribute() {
        final AuthorizationGenerator generator = new DefaultCasAuthzGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        final var profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testBadAttributeValue() {
        final AuthorizationGenerator generator = new DefaultCasAuthzGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthzGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "yes");
        final var profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsNotRemembered() {
        final AuthorizationGenerator generator = new DefaultCasAuthzGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthzGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "false");
        final var profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsRemembered() {
        final AuthorizationGenerator generator = new DefaultCasAuthzGenerator();
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthzGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "true");
        final var profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(true, profile.isRemembered());
    }
}
