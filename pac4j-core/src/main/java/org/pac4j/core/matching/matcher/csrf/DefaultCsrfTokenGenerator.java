package org.pac4j.core.matching.matcher.csrf;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;

import java.util.Optional;

/**
 * Default CSRF token generator.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultCsrfTokenGenerator implements CsrfTokenGenerator {

    @Override
    public String get(final WebContext context) {
        Optional<Object> token = getTokenFromSession(context);
        if (!token.isPresent()) {
            synchronized (this) {
                token = getTokenFromSession(context);
                if (!token.isPresent()) {
                    token = Optional.of(java.util.UUID.randomUUID().toString());
                    context.getSessionStore().set(context, Pac4jConstants.CSRF_TOKEN, token.get());
                }
            }
        }
        return (String) token.get();
    }

    protected Optional<Object> getTokenFromSession(final WebContext context) {
        return context.getSessionStore().get(context, Pac4jConstants.CSRF_TOKEN);
    }
}
