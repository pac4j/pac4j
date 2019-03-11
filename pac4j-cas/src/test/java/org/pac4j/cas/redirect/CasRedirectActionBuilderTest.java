package org.pac4j.cas.redirect;

import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests
 * @author Jerome LELEU
 * @since 3.7.0
 */
public final class CasRedirectActionBuilderTest implements TestsConstants {

    @Test
    public void testRedirect() {
        final CasRedirectActionBuilder builder = newBuilder(new CasConfiguration());
        final RedirectAction action = builder.redirect(MockWebContext.create());
        assertEquals(RedirectAction.RedirectType.REDIRECT, action.getType());
        assertEquals(LOGIN_URL + "?service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient", action.getLocation());
    }

    @Test
    public void testRedirectWithMethod() {
        final CasConfiguration config = new CasConfiguration();
        config.setMethod("post");
        final CasRedirectActionBuilder builder = newBuilder(config);
        final RedirectAction action = builder.redirect(MockWebContext.create());
        assertEquals(RedirectAction.RedirectType.REDIRECT, action.getType());
        assertEquals(LOGIN_URL + "?method=post&service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient",
            action.getLocation());
    }

    @Test
    public void testRedirectForSAMLProtocol() {
        final CasConfiguration config = new CasConfiguration();
        config.setProtocol(CasProtocol.SAML);
        final CasRedirectActionBuilder builder = newBuilder(config);
        final RedirectAction action = builder.redirect(MockWebContext.create());
        assertEquals(RedirectAction.RedirectType.REDIRECT, action.getType());
        assertEquals(LOGIN_URL + "?TARGET=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient",
            action.getLocation());
    }

    private CasRedirectActionBuilder newBuilder(final CasConfiguration config) {
        config.setLoginUrl(LOGIN_URL);
        final CasClient client = new CasClient(config);
        client.setCallbackUrl(PAC4J_URL);
        client.init();
        return (CasRedirectActionBuilder) client.getRedirectActionBuilder();
    }
}
