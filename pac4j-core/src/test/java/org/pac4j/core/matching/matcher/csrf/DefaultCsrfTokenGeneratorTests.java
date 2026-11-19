package org.pac4j.core.matching.matcher.csrf;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.authorization.authorizer.CsrfAuthorizer;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.test.context.MockWebContext;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DefaultCsrfTokenGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultCsrfTokenGeneratorTests {

    @Test
    public void testDefault() {
        val generator = new DefaultCsrfTokenGenerator();
        internalTest(generator, true);
    }

    @Test
    public void testNoRotate() {
        val generator = new DefaultCsrfTokenGenerator();
        generator.setRotateTokens(false);
        internalTest(generator, false);
    }

    @Test
    public void testRotate() {
        val generator = new DefaultCsrfTokenGenerator();
        generator.setRotateTokens(true);
        internalTest(generator, true);
    }

    @Test
    public void testNoRotateRenewsExpiredToken() {
        val generator = new DefaultCsrfTokenGenerator();
        generator.setRotateTokens(false);
        final WebContext context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        val token = generator.get(context, sessionStore);
        sessionStore.set(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE, new Date().getTime() - 1000);

        val newToken = generator.get(context, sessionStore);

        assertNotEquals(token, newToken);
        assertEquals(newToken, sessionStore.get(context, Pac4jConstants.CSRF_TOKEN).orElse(null));
        assertEquals(token, sessionStore.get(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN).orElse(null));
        final long expirationDate = (Long) sessionStore.get(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE).orElse(null);
        assertTrue(expirationDate > new Date().getTime());
    }

    @Test
    public void testNoRotateTokenAcceptedAfterExpiration() {
        val generator = new DefaultCsrfTokenGenerator();
        generator.setRotateTokens(false);
        final SessionStore sessionStore = new MockSessionStore();
        final WebContext firstContext = MockWebContext.create();
        generator.get(firstContext, sessionStore);
        sessionStore.set(firstContext, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE, new Date().getTime() - 1000);

        val postContext = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        val token = generator.get(postContext, sessionStore);
        postContext.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);

        assertTrue(new CsrfAuthorizer().isAuthorized(postContext, sessionStore, null));
    }

    private void internalTest(final DefaultCsrfTokenGenerator generator, final boolean rotate) {
        final WebContext context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        val token = generator.get(context, sessionStore);
        assertNotNull(token);
        val token2 = (String) sessionStore.get(context, Pac4jConstants.CSRF_TOKEN).orElse(null);
        assertEquals(token, token2);
        final long expirationDate = (Long) sessionStore.get(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE).orElse(null);
        val nowPlusTtl = new Date().getTime() + 1000 * generator.getTtlInSeconds();
        assertTrue(expirationDate > nowPlusTtl - 1000);
        assertTrue(expirationDate < nowPlusTtl + 1000);
        var newToken = generator.get(context, sessionStore);
        val token3 = (String) sessionStore.get(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN).orElse(null);
        val token4 = (String) sessionStore.get(context, Pac4jConstants.CSRF_TOKEN).orElse(null);
        assertEquals(token, token3);
        assertEquals(token4, newToken);
        if (rotate) {
            assertNotEquals(token, token4);
        } else {
            assertEquals(token, token4);
        }
    }
}
