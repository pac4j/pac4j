package org.pac4j.core.authorization.authorizer.csrf;

import org.pac4j.core.context.Pac4jConstants;
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
        Optional<String> token = getTokenFromSession(context);
        if (!token.isPresent()) {
            synchronized (this) {
                token = getTokenFromSession(context);
                if (!token.isPresent()) {
                    token = Optional.of(java.util.UUID.randomUUID().toString());
                    context.getSessionStore().set(context, Pac4jConstants.CSRF_TOKEN, token.get());
                }
            }
        }
        return token.get();
    }

    protected Optional<String> getTokenFromSession(final WebContext context) {
        return (Optional<String>) context.getSessionStore().get(context, Pac4jConstants.CSRF_TOKEN);
    }
}
