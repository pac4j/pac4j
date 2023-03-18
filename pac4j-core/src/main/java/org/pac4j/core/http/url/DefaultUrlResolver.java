package org.pac4j.core.http.url;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;

/**
 * Default URL resolver: use the provided URL as is or append the server and port for relative URLs.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@Getter
@Setter
public class DefaultUrlResolver implements UrlResolver {

    private boolean completeRelativeUrl;

    /**
     * <p>Constructor for DefaultUrlResolver.</p>
     */
    public DefaultUrlResolver() {}

    /**
     * <p>Constructor for DefaultUrlResolver.</p>
     *
     * @param completeRelativeUrl a boolean
     */
    public DefaultUrlResolver(final boolean completeRelativeUrl) {
        this.completeRelativeUrl = completeRelativeUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String compute(final String url, WebContext context) {
        if (this.completeRelativeUrl) {

            val relativeUrl = url != null
                && !url.startsWith(HttpConstants.SCHEME_HTTP) && !url.startsWith(HttpConstants.SCHEME_HTTPS);

            if (context != null && relativeUrl) {
                val sb = new StringBuilder();

                sb.append(context.getScheme()).append("://").append(context.getServerName());

                val notDefaultHttpPort = WebContextHelper.isHttp(context) &&
                    context.getServerPort() != HttpConstants.DEFAULT_HTTP_PORT;
                val notDefaultHttpsPort = WebContextHelper.isHttps(context) &&
                    context.getServerPort() != HttpConstants.DEFAULT_HTTPS_PORT;
                if (notDefaultHttpPort || notDefaultHttpsPort) {
                    sb.append(":").append(context.getServerPort());
                }

                sb.append(((J2EContext) context).getRequest().getContextPath());
                sb.append(url.startsWith("/") ? url : "/" + url);

                return sb.toString();
            }

        }
        return url;
    }
}
