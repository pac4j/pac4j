package org.pac4j.cas.authorization;

import lombok.val;
import org.junit.Test;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.TestsConstants;

import java.util.HashMap;
import java.util.Map;

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
        AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        Map<String, Object> attributes = new HashMap<>();
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(new CallContext(null, new MockSessionStore()), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testBadAttributeValue() {
        AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "yes");
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(new CallContext(null, new MockSessionStore()), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsNotRemembered() {
        AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "false");
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(new CallContext(null, new MockSessionStore()), profile);
        assertEquals(false, profile.isRemembered());
    }

    @Test
    public void testIsRemembered() {
        AuthorizationGenerator generator = new DefaultCasAuthorizationGenerator();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "true");
        val profile = new CasProfile();
        profile.build(ID, attributes);
        generator.generate(new CallContext(null, new MockSessionStore()), profile);
        assertEquals(true, profile.isRemembered());
    }
}
