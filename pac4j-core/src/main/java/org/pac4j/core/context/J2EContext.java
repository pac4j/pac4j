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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.exception.TechnicalException;

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

    /**
     * {@inheritDoc}
     */
    public String getRequestMethod() {
        return this.request.getMethod();
    }

    /**
     * {@inheritDoc}
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * {@inheritDoc}
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

    public String getFullRequestURL() {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
}
