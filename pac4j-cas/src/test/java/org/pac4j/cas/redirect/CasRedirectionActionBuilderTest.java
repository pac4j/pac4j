package org.pac4j.cas.redirect;

import lombok.val;
import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link CasRedirectionActionBuilder}.
 *
 * @author Jerome LELEU
 * @since 3.7.0
 */
public final class CasRedirectionActionBuilderTest implements TestsConstants {

    @Test
    public void testRedirect() {
        val builder = newBuilder(new CasConfiguration());
        val action = builder.getRedirectionAction(new CallContext(MockWebContext.create(), new MockSessionStore())).get();
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL + "?service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient",
            ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectGatewayAttribute() {
        val builder = newBuilder(new CasConfiguration());
        val context = MockWebContext.create();
        context.setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE, true);
        val action = builder.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        assertTrue(action instanceof FoundAction);
        assertTrue(((FoundAction) action).getLocation().contains("gateway=true"));
    }

    @Test
    public void testRedirectRenewAttribute() {
        val builder = newBuilder(new CasConfiguration());
        val context = MockWebContext.create();
        context.setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN, true);
        val action = builder.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        assertTrue(action instanceof FoundAction);
        assertTrue(((FoundAction) action).getLocation().contains("renew=true"));
    }

    @Test
    public void testRedirectWithMethod() {
        val config = new CasConfiguration();
        config.setMethod("post");
        val builder = newBuilder(config);
        val action = builder.getRedirectionAction(new CallContext(MockWebContext.create(), new MockSessionStore())).get();
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL + "?service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient&method=post",
            ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectForSAMLProtocol() {
        val config = new CasConfiguration();
        config.setProtocol(CasProtocol.SAML);
        val builder = newBuilder(config);
        val action = builder.getRedirectionAction(new CallContext(MockWebContext.create(), new MockSessionStore())).get();
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL + "?TARGET=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient",
            ((FoundAction) action).getLocation());
    }

    private CasRedirectionActionBuilder newBuilder(final CasConfiguration config) {
        config.setLoginUrl(LOGIN_URL);
        val client = new CasClient(config);
        client.setCallbackUrl(PAC4J_URL);
        client.init();
        return (CasRedirectionActionBuilder) client.getRedirectionActionBuilder();
    }
}
