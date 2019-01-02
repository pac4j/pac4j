package org.pac4j.core.context;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;

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
     * Get the session store.
     *
     * @return the session store
     */
    SessionStore getSessionStore();

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
     * @param name  the name of the attribute
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
     * Add a header to the response.
     *
     * @param name  name of the header
     * @param value value of the header
     */
    void setResponseHeader(String name, String value);

    /**
     * Sets the response content type.
     *
     * @param content the content type
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
     * Return whether the request is secure.
     *
     * @return whether the request is secure
     */
    boolean isSecure();

    /**
     * Return the full URL (with query string) the client used to request the server.
     *
     * @return the URL
     * @since 1.5.0
     */
    String getFullRequestURL();

    /**
     * Retrieves request cookies.
     *
     * @return the request cookies
     * @since 1.8.0
     */
    Collection<Cookie> getRequestCookies();

    /**
     * Adds cookies to the response
     *
     * @param cookie a cookie to add to the response
     * @since 1.8.0
     */
    void addResponseCookie(Cookie cookie);

    /**
     * Get the "servlet path" (in a JEE style).
     *
     * @return the "servlet path"
     * @since 1.8.1
     */
    String getPath();

    /**
     * Gets content body of the original request.
     *
     * @return the request content
     * @since 1.9.2
     */
    default String getRequestContent() {
        throw new TechnicalException("Operation not supported");
    }

    /**
     * Get the protocol version.
     *
     * @return the protocol version
     */
    default String getProtocol() {
        return "HTTP/1.0";
    }
}
