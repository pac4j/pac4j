package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * XFrame options header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XFrameOptionsMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context) {
        context.setResponseHeader("X-Frame-Options", "DENY");
        return true;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass());
    }
}
