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

import java.util.Collection;
import java.util.Map;

/**
 * This interface represents the web context to use HTTP request and session.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface WebContext {

    /**
     * Return a request parameter.
     *
     * @param name name of the parameter
     * @return the request parameter
     */
    String getRequestParameter(String name);

    /**
     * Return all request parameters.
     *
     * @return all request parameters
     */
    Map<String, String[]> getRequestParameters();

    /**
     * Return a request attribute.
     *
     * @param name the name of the attribute
     * @return the attribute
     */
    Object getRequestAttribute(String name);

    /**
     * Save a request attribute.
     *
     * @param name the name of the attribute
     * @param value the attribute
     */
    void setRequestAttribute(String name, Object value);

    /**
     * Return a request header.
     *
     * @param name name of the header
     * @return the request header
     */
    String getRequestHeader(String name);

    /**
     * Save an attribute in session.
     *
     * @param name name of the session attribute
     * @param value value of the session attribute
     */
    void setSessionAttribute(String name, Object value);

    /**
     * Get an attribute from session.
     *
     * @param name name of the session attribute
     * @return the session attribute
     */
    Object getSessionAttribute(String name);

    /**
     * Invalidate the current session.
     */
    void invalidateSession();

    /**
     * Gets the session id for this context.
     * @return the session identifier
     */
    Object getSessionIdentifier();

    /**
     * Return the request method.
     *
     * @return the request method
     */
    String getRequestMethod();

    /**
     * Return the remote address.
     *
     * @return the remote address.
     */
    String getRemoteAddr();

    /**
     * Write some content in the response.
     *
     * @param content content to write in response
     */
    void writeResponseContent(String content);

    /**
     * Set the response status.
     *
     * @param code status code to set for the response 
     */
    void setResponseStatus(int code);

    /**
     * Add a header to the response.
     *
     * @param name name of the header
     * @param value value of the header
     */
    void setResponseHeader(String name, String value);

    /**
     * Sets the response encoding type.
     * @param encoding
     */
    void setResponseCharacterEncoding(String encoding);

    /**
     * Sets the response content type.
     * @param content
     */
    void setResponseContentType(String content);

    /**
     * Return the server name.
     *
     * @return the server name
     */
    String getServerName();

    /**
     * Return the server port.
     *
     * @return the server port
     */
    int getServerPort();

    /**
     * Return the scheme.
     *
     * @return the scheme
     */
    String getScheme();

    /**
     * Return the full URL (with query string) the client used to request the server.
     *
     * @return the URL
     * @since 1.5.0
     */
    String getFullRequestURL();

    /**
     * Retrieves request cookies.
     * @since 1.8.1
     */
    Collection<Cookie> getRequestCookies();

    /**
     * Adds cookies to the response
     * @since 1.8.1
     */
    void addResponseCookie(Cookie cookie);
}



