package org.pac4j.core.logout;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;

/**
 * Indicates the logout request to perform.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class LogoutRequest {

    private HttpConstants.HTTP_METHOD method;

    private String url;

    private boolean followRedirection;

    private LogoutRequest() {}

    public static LogoutRequest get(final String url) {
        return get(url, false);
    }

    public static LogoutRequest get(final String url, final boolean followRedirection) {
        final LogoutRequest request = new LogoutRequest();
        request.url = url;
        request.method = HttpConstants.HTTP_METHOD.GET;
        request.followRedirection = followRedirection;
        return request;
    }

    public HttpConstants.HTTP_METHOD getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public boolean isFollowRedirection() {
        return followRedirection;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "method", method, "url", url, "followRedirection", followRedirection);
    }
}
