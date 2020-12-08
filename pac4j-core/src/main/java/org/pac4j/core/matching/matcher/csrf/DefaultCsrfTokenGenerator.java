package org.pac4j.core.matching.matcher.csrf;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;

import java.util.Date;

/**
 * Default CSRF token generator.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultCsrfTokenGenerator implements CsrfTokenGenerator {

    // 4 hours
    private int ttlInSeconds = 4*60*60;

    @Override
    public String get(final WebContext context) {
        final String token = CommonHelper.randomString(32);
        final long expirationDate = new Date().getTime() + ttlInSeconds * 1000;

        final SessionStore sessionStore = context.getSessionStore();
        sessionStore.set(context, Pac4jConstants.CSRF_TOKEN, token);
        sessionStore.set(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE, expirationDate);
        return token;
    }

    public int getTtlInSeconds() {
        return ttlInSeconds;
    }

    public void setTtlInSeconds(final int ttlInSeconds) {
        this.ttlInSeconds = ttlInSeconds;
    }
}
