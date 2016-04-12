package org.pac4j.core.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * This is a mocked web context to interact with request, response and session (for tests purpose).
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class MockWebContext extends BaseResponseContext {

    protected final Map<String, String> parameters = new HashMap<String, String>();

    protected final Map<String, String> headers = new HashMap<String, String>();

    protected final Map<String, Object> session = new HashMap<String, Object>();

    protected final Map<String, Object> attributes = new HashMap<String, Object>();

    protected String method = "GET";

    protected String serverName = "localhost";

    protected String scheme = "http";

    protected boolean secure = false;

    protected int serverPort = 80;

    protected String fullRequestURL = null;

    protected String ip = null;

    protected final Collection<Cookie> requestCookies = new LinkedHashSet<>();

    protected String path = "";

    protected MockWebContext() {
    }

    /**
     * Create a new instance.
     *
     * @return a new instance
     */
    public static MockWebContext create() {
        return new MockWebContext();
    }

    /**
     * Add request parameters for mock purpose.
     *
     * @param parameters parameters
     * @return this mock web context
     */
    public MockWebContext addRequestParameters(final Map<String, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    /**
     * Add a request parameter for mock purpose.
     *
     * @param key parameter name
     * @param value parameter value
     * @return this mock web context
     */
    public MockWebContext addRequestParameter(final String key, final String value) {
        this.parameters.put(key, value);
        return this;
    }

    /**
     * Add a request header for mock purpose.
     *
     * @param key request name
     * @param value request value
     * @return this mock web context
     */
    public MockWebContext addRequestHeader(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * Add a session attribute for mock purpose.
     *
     * @param name session attribute name
     * @param value session attribute value
     * @return this mock web context
     */
    public MockWebContext addSessionAttribute(final String name, final Object value) {
        setSessionAttribute(name, value);
        return this;
    }

    /**
     * Set the request method for mock purpose.
     *
     * @param method request method
     * @return this mock web context
     */
    public MockWebContext setRequestMethod(final String method) {
        this.method = method;
        return this;
    }

    @Override
    public Object getRequestAttribute(final String name) { return this.attributes.get(name); }

    @Override
    public void setRequestAttribute(final String name, final Object value) { this.attributes.put(name, value); }

    @Override
    public String getRequestParameter(final String name) {
        return this.parameters.get(name);
    }

    public MockWebContext setRemoteAddress(final String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public String getRequestHeader(final String name) {
        return this.headers.get(name);
    }

    @Override
    public void setSessionAttribute(final String name, final Object value) {
        this.session.put(name, value);
    }

    @Override
    public Object getSessionAttribute(final String name) {
        return this.session.get(name);
    }

    @Override
    public Object getSessionIdentifier() {
        return hashCode();
    }

    @Override
    public String getRequestMethod() {
        return this.method;
    }

    @Override
    public String getRemoteAddr() { return this.ip; }

    @Override
    public Map<String, String[]> getRequestParameters() {
        final Map<String, String[]> map = new HashMap<>();
        for (final Map.Entry<String, String> entry : this.parameters.entrySet()) {
            final String value = entry.getValue();
            final String[] values = new String[] { value };
            map.put(entry.getKey(), values);
        }
        return map;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    public MockWebContext setScheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }

    @Override
    public boolean isSecure() { return this.secure; }

    public MockWebContext setSecure(final boolean secure) {
        this.secure = secure;
        return this;
    }

    @Override
    public String getFullRequestURL() {
        if (fullRequestURL != null) {
            return fullRequestURL;
        } else {
            return scheme + "://" + serverName + ":" + serverPort + "/";
        }
    }

    public MockWebContext setFullRequestURL(String fullRequestURL) {
        this.fullRequestURL = fullRequestURL;
        return this;
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        return this.requestCookies;
    }

    public Collection<Cookie> getResponseCookies() { return this.responseCookies; }

    @Override
    public String getPath() {
        return path;
    }

    public MockWebContext setPath(final String path) {
        this.path = path;
        return this;
    }
}
