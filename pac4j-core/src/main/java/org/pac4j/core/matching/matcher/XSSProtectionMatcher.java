package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;

/**
 * XSS protection header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XSSProtectionMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        context.setResponseHeader("X-XSS-Protection", "1; mode=block");
        return true;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass());
    }
}
