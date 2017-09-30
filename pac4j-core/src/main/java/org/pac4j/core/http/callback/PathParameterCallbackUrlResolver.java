package org.pac4j.core.http.callback;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * The client name is added to the path of the callback URL.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class PathParameterCallbackUrlResolver extends AbstractCallbackUrlResolver {

    @Override
    public String compute(final String url, final String clientName, final WebContext context) {
        String newUrl = getUrlResolver().compute(url, context);
        if (newUrl != null) {
            if (!newUrl.endsWith("/")) {
                newUrl += "/";
            }
            newUrl += clientName;
        }
        return newUrl;
    }

    @Override
    public boolean matches(final String clientName, final WebContext context) {
        final String path = context.getPath();
        if (path != null) {
            final int pos = path.lastIndexOf("/");
            final String name;
            if (pos >= 0) {
                name = path.substring(pos + 1);
            } else {
                name = path;
            }
            return CommonHelper.areEqualsIgnoreCaseAndTrim(name, clientName);
        }
        return false;
    }
}
