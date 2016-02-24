package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link CheckProfileTypeAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckProfileTypeAuthorizerTests {

    class FakeProfile1 extends CommonProfile {
    }
    class FakeProfile2 extends CommonProfile {
    }

    @Test
    public void testGoodProfile() {
        final CheckProfileTypeAuthorizer authorizer = new CheckProfileTypeAuthorizer(FakeProfile1.class, FakeProfile2.class);
        assertTrue(authorizer.isAuthorized(null, new FakeProfile1()));
    }

    @Test
    public void testBadProfileType() {
        final CheckProfileTypeAuthorizer authorizer = new CheckProfileTypeAuthorizer(FakeProfile1.class);
        assertFalse(authorizer.isAuthorized(null, new FakeProfile2()));
    }
}
