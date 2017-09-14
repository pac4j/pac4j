package org.pac4j.core.authorization.authorizer.csrf;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;

/**
 * Default CSRF token generator.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultCsrfTokenGenerator implements CsrfTokenGenerator {

    @Override
    public String get(final WebContext context) {
        String token = getTokenFromSession(context);
        if (token == null) {
            synchronized (this) {
                token = getTokenFromSession(context);
                if (token == null) {
                    token = java.util.UUID.randomUUID().toString();
                    context.getSessionStore().set(context, Pac4jConstants.CSRF_TOKEN, token);
                }
            }
        }
        return token;
    }

    protected String getTokenFromSession(final WebContext context) {
        return (String) context.getSessionStore().get(context, Pac4jConstants.CSRF_TOKEN);
    }
}
