package org.pac4j.core.http;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;

/**
 * The new callback url is computed from a relative callback url and the current server configuration.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class RelativeCallbackUrlResolver implements CallbackUrlResolver {

    @Override
    public String compute(final String callbackUrl, WebContext context) {
        if (context != null && callbackUrl != null && !callbackUrl.startsWith("http://") && !callbackUrl.startsWith("https://")) {
            final StringBuilder sb = new StringBuilder();

            sb.append(context.getScheme()).append("://").append(context.getServerName());

            if((ContextHelper.isHttp(context) && context.getServerPort() != HttpConstants.DEFAULT_PORT) || 
              (ContextHelper.isHttps(context) && context.getServerPort() != HttpConstants.DEFAULT_HTTPS_PORT)) {
                sb.append(":").append(context.getServerPort());
            }

            sb.append(callbackUrl.startsWith("/") ? callbackUrl : "/" + callbackUrl);

            return sb.toString();
        }

        return callbackUrl;
    }
}
