package org.pac4j.core.context;

import org.pac4j.core.util.CommonHelper;

import java.util.Collection;

/**
 * A helper for the web context.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class ContextHelper implements HttpConstants {

    /**
     * Get a specific cookie by its name.
     *
     * @param cookies provided cookies
     * @param name the name of the cookie
     * @return the cookie
     */
    public static Cookie getCookie(final Collection<Cookie> cookies, final String name) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie != null && CommonHelper.areEquals(name, cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * Get a specific cookie by its name.
     *
     * @param context the current web context
     * @param name the name of the cookie
     * @return the cookie
     */
    public static Cookie getCookie(final WebContext context, final String name) {
        return getCookie(context.getRequestCookies(), name);
    }

    /**
     * Whether it is a GET request.
     *
     * @param context the web context
     * @return whether it is a GET request
     */
    public static boolean isGet(final WebContext context) {
        return HttpConstants.HTTP_METHOD.GET.name().equalsIgnoreCase(context.getRequestMethod());
    }

    /**
     * Whether it is a POST request.
     *
     * @param context the web context
     * @return whether it is a POST request
     */
    public static boolean isPost(final WebContext context) {
        return HttpConstants.HTTP_METHOD.POST.name().equalsIgnoreCase(context.getRequestMethod());
    }

    /**
     * Whether it is a PUT request.
     *
     * @param context the web context
     * @return whether it is a PUT request
     */
    public static boolean isPut(final WebContext context) {
        return HttpConstants.HTTP_METHOD.PUT.name().equalsIgnoreCase(context.getRequestMethod());
    }

    /**
     * Whether it is a PATCH request.
     *
     * @param context the web context
     * @return whether it is a PATCH request
     */
    public static boolean isPatch(final WebContext context) {
        return HttpConstants.HTTP_METHOD.PATCH.name().equalsIgnoreCase(context.getRequestMethod());
    }

    /**
     * Whether it is a DELETE request.
     *
     * @param context the web context
     * @return whether it is a DELETE request
     */
    public static boolean isDelete(final WebContext context) {
        return HttpConstants.HTTP_METHOD.DELETE.name().equalsIgnoreCase(context.getRequestMethod());
    }

    /**
     * Whether the request is HTTPS or secure.
     *
     * @param context the current web context
     * @return whether the request is HTTPS or secure
     */
    public static boolean isHttpsOrSecure(final WebContext context) {
        return SCHEME_HTTPS.equalsIgnoreCase(context.getScheme()) || context.isSecure();
    }

    /**
     * Whether the request is HTTP.
     *
     * @param context the current web context
     * @return whether the request is HTTP
     */
    public static boolean isHttp(final WebContext context) {
        return SCHEME_HTTP.equalsIgnoreCase(context.getScheme());
    }

    /**
     * Whether the request is HTTPS.
     *
     * @param context the current web context
     * @return whether the request is HTTPS
     */
    public static boolean isHttps(final WebContext context) {
        return SCHEME_HTTPS.equalsIgnoreCase(context.getScheme());
    }
}
