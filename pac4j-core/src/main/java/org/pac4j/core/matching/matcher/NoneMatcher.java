package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;

/**
 * A pass-through matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class NoneMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context) {
        return true;
    }
}
