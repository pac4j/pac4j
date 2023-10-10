package org.pac4j.core.context;

import lombok.val;
import org.pac4j.core.util.CommonHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A helper for the web context.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class WebContextHelper implements HttpConstants {

    private static ZoneId GMT = ZoneId.of("GMT");
    /**
     * Date formats with time zone as specified in the HTTP RFC to use for formatting.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);

    /**
     * Get a specific cookie by its name.
     *
     * @param cookies provided cookies
     * @param name the name of the cookie
     * @return the cookie
     */
    public static Cookie getCookie(final Iterable<Cookie> cookies, final String name) {
        if (cookies != null) {
            for (val cookie : cookies) {
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

    /**
     * Custom method for adding cookie because the servlet-api version doesn't support SameSite attributes.
     * Sets the default SameSite policy to lax which is what most browsers do if the cookie doesn't specify
     * a SameSite policy.
     *
     * @param cookie pac4j Cookie object
     * @return a {@link String} object
     */
    public static String createCookieHeader(Cookie cookie) {
        var builder = new StringBuilder();
        builder.append(String.format("%s=%s;", cookie.getName(), cookie.getValue()));

        if (cookie.getMaxAge() > -1) {
            builder.append(String.format(" Max-Age=%s;", cookie.getMaxAge()));
            long millis = cookie.getMaxAge() > 0 ? System.currentTimeMillis() + (cookie.getMaxAge() * 1000) : 0;
            Instant instant = Instant.ofEpochMilli(millis);
            ZonedDateTime time = ZonedDateTime.ofInstant(instant, GMT);
            builder.append(String.format(" Expires=%s;", DATE_FORMATTER.format(time)));
        }
        if (CommonHelper.isNotBlank(cookie.getDomain())) {
            builder.append(String.format(" Domain=%s;", cookie.getDomain()));
        }
        builder.append(String.format(" Path=%s;", CommonHelper.isNotBlank(cookie.getPath()) ? cookie.getPath() : "/"));

        var sameSitePolicy = cookie.getSameSitePolicy() == null ? "lax" : cookie.getSameSitePolicy().toLowerCase();
        switch (sameSitePolicy) {
            case "strict" -> builder.append(" SameSite=Strict;");
            case "none" -> builder.append(" SameSite=None;");
            default -> builder.append(" SameSite=Lax;");
        }
        if (cookie.isSecure() || "none".equals(sameSitePolicy)) {
            builder.append(" Secure;");
        }
        if (cookie.isHttpOnly()) {
            builder.append(" HttpOnly;");
        }
        var value = builder.toString();
        if (value.endsWith(";")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    /**
     * Checks whether this parameter is part of the query string.
     *
     * @param context the web context
     * @param name the parameter name
     * @return whether this parameter is part of the query string
     */
    public static boolean isQueryStringParameter(final WebContext context, final String name) {
        val queryString = context.getQueryString();
        if (queryString.isPresent()) {
            return context.getRequestParameter(name).isPresent() && queryString.get().contains(name + '=');
        }
        return false;
    }
}
