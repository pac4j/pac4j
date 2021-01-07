package org.pac4j.cas.redirect;

import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link CasRedirectionActionBuilder}.
 *
 * @author Jerome LELEU
 * @since 3.7.0
 */
public final class CasRedirectionActionBuilderTest implements TestsConstants {

    @Test
    public void testRedirect() {
        final CasRedirectionActionBuilder builder = newBuilder(new CasConfiguration());
        final RedirectionAction action = builder.getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL + "?service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient",
            ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectWithMethod() {
        final CasConfiguration config = new CasConfiguration();
        config.setMethod("post");
        final CasRedirectionActionBuilder builder = newBuilder(config);
        final RedirectionAction action = builder.getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL + "?service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient&method=post",
            ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectForSAMLProtocol() {
        final CasConfiguration config = new CasConfiguration();
        config.setProtocol(CasProtocol.SAML);
        final CasRedirectionActionBuilder builder = newBuilder(config);
        final RedirectionAction action = builder.getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL + "?TARGET=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient",
            ((FoundAction) action).getLocation());
    }

    private CasRedirectionActionBuilder newBuilder(final CasConfiguration config) {
        config.setLoginUrl(LOGIN_URL);
        final CasClient client = new CasClient(config);
        client.setCallbackUrl(PAC4J_URL);
        client.init();
        return (CasRedirectionActionBuilder) client.getRedirectionActionBuilder();
    }
}
