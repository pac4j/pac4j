package org.pac4j.core.context;

import org.pac4j.core.exception.TechnicalException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * This interface represents the web context to use HTTP request and response.
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
    Optional<String> getRequestParameter(String name);

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
    Optional getRequestAttribute(String name);

    /**
     * Return a request attribute typed to a strong type.
     * The attribute, if found, will be casted down to the given type.
     * @param <T>   the type parameter
     * @param name  the name of the attribute
     * @param clazz the clazz
     * @return the attribute
     */
    default <T> Optional<T> getRequestAttribute(final String name, final Class<T> clazz) {
        return getRequestAttribute(name).map(clazz::cast);
    }

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
    Optional<String> getRequestHeader(String name);

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
     * Get a header from the response.
     *
     * @param name  name of the header
     * @return the value of the header
     */
    Optional<String> getResponseHeader(String name);

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
     * Return the full URL (without the query string) the client used to request the server.
     *
     * @return the URL
     * @since 5.1.2
     */
    default String getRequestURL() {
        var idx = getFullRequestURL().indexOf('?');
        if (idx != -1) {
            return getFullRequestURL().substring(0, idx);
        }
        return getFullRequestURL();
    }

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

    /**
     * Get the query string.
     *
     * @return the query string
     */
    Optional<String> getQueryString();
}
