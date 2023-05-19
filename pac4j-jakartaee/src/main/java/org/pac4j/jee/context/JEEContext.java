package org.pac4j.jee.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

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

    /** {@inheritDoc} */
    @Override
    public Optional<String> getRequestParameter(final String name) {
        return Optional.ofNullable(this.request.getParameter(name));
    }

    /** {@inheritDoc} */
    @Override
    public Optional getRequestAttribute(final String name) {
        return Optional.ofNullable(this.request.getAttribute(name));
    }

    /** {@inheritDoc} */
    @Override
    public void setRequestAttribute(final String name, final Object value) {
        this.request.setAttribute(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String[]> getRequestParameters() {
        return this.request.getParameterMap();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getRequestHeader(final String name) {
        val names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                val headerName = names.nextElement();
                if (headerName != null && headerName.equalsIgnoreCase(name)) {
                    return Optional.ofNullable(this.request.getHeader(headerName));
                }
            }
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public String getRequestMethod() {
        return this.request.getMethod();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void setResponseHeader(final String name, final String value) {
        this.response.setHeader(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getResponseHeader(final String name) {
        return Optional.ofNullable(this.response.getHeader(name));
    }

    /** {@inheritDoc} */
    @Override
    public void setResponseContentType(final String content) {
        this.response.setContentType(content);
    }

    /** {@inheritDoc} */
    @Override
    public String getServerName() {
        return this.request.getServerName();
    }

    /** {@inheritDoc} */
    @Override
    public int getServerPort() {
        return this.request.getServerPort();
    }

    /** {@inheritDoc} */
    @Override
    public String getScheme() {
        return this.request.getScheme();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSecure() {
        return this.request.isSecure();
    }

    /** {@inheritDoc} */
    @Override
    public String getRequestURL() {
        val url = request.getRequestURL().toString();
        var idx = url.indexOf('?');
        if (idx != -1) {
            return url.substring(0, idx);
        }
        return url;
    }

    /** {@inheritDoc} */
    @Override
    public String getFullRequestURL() {
        val requestURL = request.getRequestURL();
        val queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        }
        return requestURL.append('?').append(queryString).toString();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Cookie> getRequestCookies() {
        final Collection<Cookie> pac4jCookies = new LinkedHashSet<>();
        val cookies = this.request.getCookies();

        if (cookies != null) {
            for (val c : cookies) {
                val cookie = new Cookie(c.getName(), c.getValue());
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

    /** {@inheritDoc} */
    @Override
    public void addResponseCookie(Cookie cookie) {
        this.response.addHeader("Set-Cookie", WebContextHelper.createCookieHeader(cookie));
    }

    /**
     * {@inheritDoc}
     *
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
        val context = request.getContextPath();
        // this one shouldn't be null either, but in case it is, then let's consider it is empty
        if (context != null) {
            return fullPath.substring(context.length());
        }
        return fullPath;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public String getProtocol() {
        return request.getProtocol();
    }
}
