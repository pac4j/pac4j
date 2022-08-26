package org.pac4j.jee.context;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

/**
 * This implementation uses the JEE {@link HttpServletRequest} and {@link HttpServletResponse}.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class JEEContext implements WebContext {

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
    public String getRequestURL() {
        final var url = request.getRequestURL().toString();
        var idx = url.indexOf('?');
        if (idx != -1) {
            return url.substring(0, idx);
        }
        return url;
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
        this.response.addHeader("Set-Cookie", WebContextHelper.createCookieHeader(cookie));
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
            return Pac4jConstants.EMPTY_STRING;
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
                    .reduce(Pac4jConstants.EMPTY_STRING, String::concat);
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
