/*
  Copyright 2012 - 2014 Jerome Leleu

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
     * @param name
     * @return the request parameter
     */
    public String getRequestParameter(String name);

    /**
     * Return all request parameters.
     *
     * @return all request parameters
     */
    public Map<String, String[]> getRequestParameters();

    /**
     * Return a request header.
     *
     * @param name
     * @return the request header
     */
    public String getRequestHeader(String name);

    /**
     * Save an attribute in session.
     *
     * @param name
     * @param value
     */
    public void setSessionAttribute(String name, Object value);

    /**
     * Get an attribute from session.
     *
     * @param name
     * @return the session attribute
     */
    public Object getSessionAttribute(String name);

    /**
     * Return the request method.
     *
     * @return the request method
     */
    public String getRequestMethod();

    /**
     * Write some content in the response.
     *
     * @param content
     */
    public void writeResponseContent(String content);

    /**
     * Set the response status.
     *
     * @param code
     */
    public void setResponseStatus(int code);

    /**
     * Add a header to the response.
     *
     * @param name
     * @param value
     */
    public void setResponseHeader(String name, String value);

    /**
     * Return the server name.
     *
     * @return the server name
     */
    public String getServerName();

    /**
     * Return the server port.
     *
     * @return the server port
     */
    public int getServerPort();

    /**
     * Return the scheme.
     *
     * @return the scheme
     */
    public String getScheme();

    /**
     * Return the full URL (with query string) the client used to request the server.
     * 
     * @return the URL
     * @since 1.5.0
     */
    public String getFullRequestURL();

}
