package org.pac4j.core.matching.matcher.csrf;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Optional;

/**
 * Default CSRF token generator.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultCsrfTokenGenerator implements CsrfTokenGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCsrfTokenGenerator.class);

    // 4 hours
    private int ttlInSeconds = 4*60*60;

    @Override
    public String get(final WebContext context, final SessionStore sessionStore) {
        final String token = CommonHelper.randomString(32);
        LOGGER.debug("generated CSRF token: {}", token);
        final long expirationDate = new Date().getTime() + ttlInSeconds * 1000;

        final Optional<Object> oldToken = sessionStore.get(context, Pac4jConstants.CSRF_TOKEN);
        if (oldToken.isPresent()) {
            sessionStore.set(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN, oldToken.get());
        }
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
