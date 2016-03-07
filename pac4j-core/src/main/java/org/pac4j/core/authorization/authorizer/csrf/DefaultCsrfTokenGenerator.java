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
        String token = (String) context.getSessionAttribute(Pac4jConstants.CSRF_TOKEN);
        if (token == null) {
            synchronized (this) {
                token = (String) context.getSessionAttribute(Pac4jConstants.CSRF_TOKEN);
                if (token == null) {
                    token = java.util.UUID.randomUUID().toString();
                    context.setSessionAttribute(Pac4jConstants.CSRF_TOKEN, token);
                }
            }
        }
        return token;
    }
}
