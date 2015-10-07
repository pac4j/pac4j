/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.context;

import org.pac4j.core.exception.TechnicalException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * This implementation uses the J2E request and session.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class J2EContext implements WebContext {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    /**
     * Build a J2E context from the current HTTP request.
     *
     * @param request the current request
     * @param response the current response
     */
    public J2EContext(final HttpServletRequest request, final HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestParameter(final String name) {
        return this.request.getParameter(name);
    }

    /**
     * {@inheritDoc}
     */
    public Object getRequestAttribute(final String name) { return this.request.getAttribute(name); }

    /**
     * {@inheritDoc}
     */
    public void setRequestAttribute(final String name, final Object value) { this.request.setAttribute(name, value); }

    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getRequestParameters() {
        return this.request.getParameterMap();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestHeader(final String name) {
        return this.request.getHeader(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setSessionAttribute(final String name, final Object value) {
        this.request.getSession().setAttribute(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object getSessionAttribute(final String name) {
        return this.request.getSession().getAttribute(name);
    }

    @Override
    public Object getSessionIdentifier() {
        return this.request.getSession().getId();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestMethod() {
        return this.request.getMethod();
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteAddr() { return this.request.getRemoteAddr(); }

    /**
     * Return the HTTP request.
     *
     * @return the HTTP request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * Return the HTTP response.
     *
     * @return the HTTP response
     */
    public HttpServletResponse getResponse() {
        return this.response;
    }

    /**
     * {@inheritDoc}
     */
    public void writeResponseContent(final String content) {
        if (content != null) {
            try {
                this.response.getWriter().write(content);
            } catch (final IOException e) {
                throw new TechnicalException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setResponseStatus(final int code) {
        if (code == HttpConstants.OK || code == HttpConstants.TEMP_REDIRECT) {
            this.response.setStatus(code);
        } else {
            try {
                this.response.sendError(code);
            } catch (final IOException e) {
                throw new TechnicalException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setResponseHeader(final String name, final String value) {
        this.response.setHeader(name, value);
    }

    @Override
    public void setResponseCharacterEncoding(final String encoding) {
        this.response.setCharacterEncoding(encoding);
    }

    @Override
    public void setResponseContentType(final String content) {
        this.response.setContentType(content);
    }

    /**
     * {@inheritDoc}
     */
    public String getServerName() {
        return this.request.getServerName();
    }

    /**
     * {@inheritDoc}
     */
    public int getServerPort() {
        return this.request.getServerPort();
    }

    /**
     * {@inheritDoc}
     */
    public String getScheme() {
        return this.request.getScheme();
    }

    /**
     * {@inheritDoc}
     */
    public String getFullRequestURL() {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        final javax.servlet.http.Cookie[] cookies = this.request.getCookies();
        final Collection<Cookie> pac4jCookies = new LinkedHashSet<>(cookies.length);
        for (javax.servlet.http.Cookie c : this.request.getCookies()) {
            final Cookie cookie = new Cookie(c.getName(), c.getValue());
            cookie.setComment(c.getComment());
            cookie.setDomain(c.getDomain());
            cookie.setHttpOnly(c.isHttpOnly());
            cookie.setMaxAge(c.getMaxAge());
            cookie.setPath(c.getPath());
            cookie.setSecure(c.getSecure());
            pac4jCookies.add(cookie);
        }
        return pac4jCookies;
    }

    @Override
    public void addResponseCookie(final Cookie cookie) {
        javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
        c.setSecure(cookie.isSecure());
        c.setPath(cookie.getPath());
        c.setMaxAge(cookie.getMaxAge());
        c.setHttpOnly(cookie.isHttpOnly());
        c.setComment(cookie.getComment());
        c.setDomain(cookie.getDomain());
        this.response.addCookie(c);
    }
}
