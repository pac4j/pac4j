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

import org.pac4j.core.context.session.J2ESessionStore;
import org.pac4j.core.context.session.SessionStore;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This partial J2E request context is not used by any implementation. It might be removed in the future.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 * @deprecated
 */
@Deprecated
public class J2ERequestContext extends BaseResponseContext {

    private final HttpServletRequest request;

    private final SessionStore sessionStore;

    public J2ERequestContext(final HttpServletRequest request) {
        this(request, null);
    }

    public J2ERequestContext(final HttpServletRequest request, final SessionStore sessionStore) {
        this.request = request;
        if (sessionStore == null) {
            this.sessionStore = new J2ESessionStore();
        } else {
            this.sessionStore = sessionStore;
        }
    }

    @Override
    public String getRequestParameter(final String name) {
        return this.request.getParameter(name);
    }

    @Override
    public Map<String, String[]> getRequestParameters() {
        return this.request.getParameterMap();
    }

    @Override
    public Object getRequestAttribute(final String name) { return this.request.getAttribute(name); }

    @Override
    public void setRequestAttribute(final String name, final Object value) { this.request.setAttribute(name, value); }

    @Override
    public String getRequestHeader(final String name) {
        return this.request.getHeader(name);
    }

    @Override
    public void setSessionAttribute(final String name, final Object value) {
        sessionStore.set(this, name, value);
    }

    @Override
    public Object getSessionAttribute(final String name) {
        return sessionStore.get(this, name);
    }

    @Override
    public Object getSessionIdentifier() {
        return sessionStore.getOrCreateSessionId(this);
    }

    @Override
    public String getRequestMethod() {
        return this.request.getMethod();
    }

    @Override
    public String getRemoteAddr() { return this.request.getRemoteAddr(); }

    /**
     * Return the HTTP request.
     *
     * @return the HTTP request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    public SessionStore getSessionStore() {
        return sessionStore;
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
    public boolean isSecure() { return this.request.isSecure(); }

    @Override
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
    public String getPath() {
        return request.getServletPath();
    }
}
