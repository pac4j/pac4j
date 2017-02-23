package org.pac4j.core.http;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;

/**
 * The new URL is computed from a relative URL and the current server configuration.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class RelativeUrlResolver implements UrlResolver {

    @Override
    public String compute(final String url, WebContext context) {
        if (context != null && url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            final StringBuilder sb = new StringBuilder();

            sb.append(context.getScheme()).append("://").append(context.getServerName());

            final boolean notDefaultHttpPort = ContextHelper.isHttp(context) && context.getServerPort() != HttpConstants.DEFAULT_HTTP_PORT;
            final boolean notDefaultHttpsPort = ContextHelper.isHttps(context) && context.getServerPort() != HttpConstants.DEFAULT_HTTPS_PORT;
            if (notDefaultHttpPort || notDefaultHttpsPort) {
                sb.append(":").append(context.getServerPort());
            }

            sb.append(url.startsWith("/") ? url : "/" + url);

            return sb.toString();
        }

        return url;
    }
}
