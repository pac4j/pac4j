package org.pac4j.core.context;

import java.util.*;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * This is a mocked web context to interact with request, response and session (for tests purpose).
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class MockWebContext implements WebContext {

    protected final Map<String, String> parameters = new HashMap<>();

    protected final Map<String, String> headers = new HashMap<>();

    protected final Map<String, Object> attributes = new HashMap<>();

    protected String method = HTTP_METHOD.GET.name();

    protected String serverName = "localhost";

    protected String scheme = SCHEME_HTTP;

    protected boolean secure = false;

    protected int serverPort = DEFAULT_HTTP_PORT;

    protected String fullRequestURL = null;

    protected String ip = null;

    protected final Collection<Cookie> requestCookies = new LinkedHashSet<>();

    protected String path = "";

    protected String requestContent;

    protected String responseContent = "";

    protected int responseStatus = -1;

    protected final Map<String, String> responseHeaders = new HashMap<>();

    protected final Collection<Cookie> responseCookies = new LinkedHashSet<>();

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
    public Optional getRequestAttribute(final String name) {
        return Optional.ofNullable(this.attributes.get(name));
    }

    @Override
    public void setRequestAttribute(final String name, final Object value) { this.attributes.put(name, value); }

    @Override
    public Optional<String> getRequestParameter(final String name) {
        return Optional.ofNullable(this.parameters.get(name));
    }

    public MockWebContext setRemoteAddress(final String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public Optional<String> getRequestHeader(final String name) {
        return Optional.ofNullable(this.headers.get(name));
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
        for (final var entry : this.parameters.entrySet()) {
            final var value = entry.getValue();
            final var values = new String[] {value};
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

    @Override
    public void setResponseHeader(final String name, final String value) {
        this.responseHeaders.put(name, value);
    }

    @Override
    public Optional<String> getResponseHeader(final String name) {
        return Optional.ofNullable(this.responseHeaders.get(name));
    }

    public String getResponseContent() {
        return this.responseContent;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    public Map<String, String> getResponseHeaders() {
        return this.responseHeaders;
    }

    @Override
    public void setResponseContentType(final String content) {}

    @Override
    public void addResponseCookie(Cookie cookie) {
        this.responseCookies.add(cookie);
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    @Override
    public String getRequestContent() {
        if (requestContent == null) {
            return WebContext.super.getRequestContent();
        } else {
            return requestContent;
        }
    }


}
