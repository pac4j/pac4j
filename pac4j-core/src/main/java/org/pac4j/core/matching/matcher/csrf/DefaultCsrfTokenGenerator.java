package org.pac4j.core.matching.matcher.csrf;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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

    private boolean rotateTokens = true;

    @Override
    public String get(final WebContext context, final SessionStore sessionStore) {
        String token = null;

        final var optCurrentToken = sessionStore.get(context, Pac4jConstants.CSRF_TOKEN);
        if (optCurrentToken.isPresent()) {
            token = (String) optCurrentToken.get();
            LOGGER.debug("previous CSRF token: {}", token);
            sessionStore.set(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN, token);
        } else {
            sessionStore.set(context, Pac4jConstants.PREVIOUS_CSRF_TOKEN, null);
        }

        if (optCurrentToken.isEmpty() || rotateTokens) {
            token = CommonHelper.randomString(32);
            LOGGER.debug("generated CSRF token: {} for current URL: {}", token, context.getFullRequestURL());
            final var expirationDate = new Date().getTime() + ttlInSeconds * 1000;

            sessionStore.set(context, Pac4jConstants.CSRF_TOKEN, token);
            sessionStore.set(context, Pac4jConstants.CSRF_TOKEN_EXPIRATION_DATE, expirationDate);
        }
        return token;
    }

    public int getTtlInSeconds() {
        return ttlInSeconds;
    }

    public void setTtlInSeconds(final int ttlInSeconds) {
        this.ttlInSeconds = ttlInSeconds;
    }

    public boolean isRotateTokens() {
        return rotateTokens;
    }

    public void setRotateTokens(final boolean rotateTokens) {
        this.rotateTokens = rotateTokens;
    }
}
