package org.pac4j.core.http.callback;

import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;

/**
 * The client name is added to the path of the callback URL.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class PathParameterCallbackUrlResolver implements CallbackUrlResolver {

    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
        var newUrl = urlResolver.compute(url, context);
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
        val path = context.getPath();
        if (path != null) {
            val pos = path.lastIndexOf("/");
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
