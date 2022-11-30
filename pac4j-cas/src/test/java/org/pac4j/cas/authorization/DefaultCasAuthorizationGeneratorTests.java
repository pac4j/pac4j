package org.pac4j.cas.authorization;

import lombok.val;
import org.junit.Test;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.TestsConstants;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link DefaultCasAuthorizationGenerator}.
 *
 * @author Michael Remond
 * @since 1.5.1
 */
public final class DefaultCasAuthorizationGeneratorTests implements TestsConstants {

    @Test
    public void testNoAttribute() {
        val generator = new DefaultCasAuthorizationGenerator();
        val attributes = new HashMap<String, Object>();
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testBadAttributeValue() {
        val generator = new DefaultCasAuthorizationGenerator();
        val attributes = new HashMap<String, Object>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "yes");
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsNotRemembered() {
        val generator = new DefaultCasAuthorizationGenerator();
        val attributes = new HashMap<String, Object>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "false");
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsRemembered() {
        val generator = new DefaultCasAuthorizationGenerator();
        val attributes = new HashMap<String, Object>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "true");
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(null, new MockSessionStore(), profile);
        assertEquals(true, profile.isRemembered());
    }
}
