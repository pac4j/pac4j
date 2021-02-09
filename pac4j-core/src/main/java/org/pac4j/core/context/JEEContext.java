package org.pac4j.core.context;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This implementation uses the JEE {@link HttpServletRequest} and {@link HttpServletResponse}.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class JEEContext implements WebContext {

    private static ZoneId GMT = ZoneId.of("GMT");

    /**
     * Date formats with time zone as specified in the HTTP RFC to use for formatting.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);

    /**
     * Custom method for adding cookie because the servlet-api version doesn't support SameSite attributes.
     * @param cookie pac4j Cookie object
     */
    private String createCookieHeader(Cookie cookie) {
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

        var sameSitePolicy = cookie.getSameSitePolicy() == null ? "none" : cookie.getSameSitePolicy().toLowerCase();
        switch (sameSitePolicy) {
            case "strict":
                builder.append(" SameSite=Strict;");
                break;
            case "lax":
                builder.append(" SameSite=Lax;");
                break;
            case "none":
            default:
                builder.append(" SameSite=None;");
                break;
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

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private String body;

    /**
     * Build a JEE context from the current HTTP request and response.
     *
     * @param request  the current request
     * @param response the current response
     */
    public JEEContext(final HttpServletRequest request, final HttpServletResponse response) {
        CommonHelper.assertNotNull("request", request);
        CommonHelper.assertNotNull("response", response);
        this.request = request;
        this.response = response;
    }

    @Override
    public Optional<String> getRequestParameter(final String name) {
        return Optional.ofNullable(this.request.getParameter(name));
    }

    @Override
    public Optional getRequestAttribute(final String name) {
        return Optional.ofNullable(this.request.getAttribute(name));
    }

    @Override
    public void setRequestAttribute(final String name, final Object value) {
        this.request.setAttribute(name, value);
    }

    @Override
    public Map<String, String[]> getRequestParameters() {
        return this.request.getParameterMap();
    }

    @Override
    public Optional<String> getRequestHeader(final String name) {
        final var names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                final var headerName = names.nextElement();
                if (headerName != null && headerName.equalsIgnoreCase(name)) {
                    return Optional.ofNullable(this.request.getHeader(headerName));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String getRequestMethod() {
        return this.request.getMethod();
    }

    @Override
    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }

    /**
     * Return the native HTTP request.
     *
     * @return the native HTTP request
     */
    public HttpServletRequest getNativeRequest() {
        return this.request;
    }

    /**
     * Return the native HTTP response.
     *
     * @return the native HTTP response
     */
    public HttpServletResponse getNativeResponse() {
        return this.response;
    }

    @Override
    public void setResponseHeader(final String name, final String value) {
        this.response.setHeader(name, value);
    }

    @Override
    public Optional<String> getResponseHeader(final String name) {
        return Optional.ofNullable(this.response.getHeader(name));
    }

    @Override
    public void setResponseContentType(final String content) {
        this.response.setContentType(content);
    }

    @Override
    public String getServerName() {
        return this.request.getServerName();
    }

    @Override
    public int getServerPort() {
        return this.request.getServerPort();
    }

    @Override
    public String getScheme() {
        return this.request.getScheme();
    }

    @Override
    public boolean isSecure() {
        return this.request.isSecure();
    }

    @Override
    public String getFullRequestURL() {
        final var requestURL = request.getRequestURL();
        final var queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        }
        return requestURL.append('?').append(queryString).toString();
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        final Collection<Cookie> pac4jCookies = new LinkedHashSet<>();
        final var cookies = this.request.getCookies();

        if (cookies != null) {
            for (final var c : cookies) {
                final var cookie = new Cookie(c.getName(), c.getValue());
                cookie.setDomain(c.getDomain());
                cookie.setHttpOnly(c.isHttpOnly());
                cookie.setMaxAge(c.getMaxAge());
                cookie.setPath(c.getPath());
                cookie.setSecure(c.getSecure());
                pac4jCookies.add(cookie);
            }
        }
        return pac4jCookies;
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        this.response.addHeader("Set-Cookie", createCookieHeader(cookie));
    }

    /**
     * This is not implemented using {@link HttpServletRequest#getServletPath()} or
     * {@link HttpServletRequest#getPathInfo()} because they both have strange behaviours
     * in different contexts (inside servlets, inside filters, various container implementation, etc)
     */
    @Override
    public String getPath() {
        var fullPath = request.getRequestURI();
        // it shouldn't be null, but in case it is, it's better to return empty string
        if (fullPath == null) {
            return "";
        }
        // very strange use case
        if (fullPath.startsWith("//")) {
            fullPath = fullPath.substring(1);
        }
        final var context = request.getContextPath();
        // this one shouldn't be null either, but in case it is, then let's consider it is empty
        if (context != null) {
            return fullPath.substring(context.length());
        }
        return fullPath;
    }

    @Override
    public String getRequestContent() {
        if (body == null) {
            try {
                body = request.getReader()
                    .lines()
                    .reduce("", String::concat);
            } catch (final IOException e) {
                throw new TechnicalException(e);
            }
        }
        return body;
    }

    @Override
    public String getProtocol() {
        return request.getProtocol();
    }
}
