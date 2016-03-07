package org.pac4j.http.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.authorization.authorizer.IpRegexpAuthorizer;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpRegexpAuthorizer}.
 * 
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class IpRegexpAuthorizerTests {

    private final static String GOOD_IP = "127.0.0.1";
    private final static String BAD_IP = "192.168.0.1";

    private final static IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer(GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        final IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer();
        authorizer.isAuthorized(MockWebContext.create(), null);
    }

    @Test
    public void testValidateGoodIP() {
        assertTrue(authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(GOOD_IP), null));
    }

    @Test
    public void testValidateBadIP() {
        assertFalse(authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(BAD_IP), null));
    }
}
