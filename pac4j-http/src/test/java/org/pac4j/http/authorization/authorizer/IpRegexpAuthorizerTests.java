package org.pac4j.http.authorization.authorizer;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the {@link IpRegexpAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class IpRegexpAuthorizerTests {

    private final static String GOOD_IP = "goodIp";
    private final static String BAD_IP = "badIp";

    private final static IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer(GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        val authorizer = new IpRegexpAuthorizer();
        authorizer.isAuthorized(MockWebContext.create(), new MockSessionStore(), null);
    }

    @Test
    public void testValidateGoodIP() {
        assertTrue(authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(GOOD_IP), new MockSessionStore(), null));
    }

    @Test
    public void testValidateBadIP() {
        assertFalse(authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(BAD_IP), new MockSessionStore(), null));
    }
}
