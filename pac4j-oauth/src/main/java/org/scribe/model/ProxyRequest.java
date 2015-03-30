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
package org.scribe.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.exceptions.OAuthException;

/**
 * This class represents a Scribe {@link org.scribe.model.Request} with proxy capabilities. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @version 1.2.0
 */
class ProxyRequest {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    
    private final String url;
    private final Verb verb;
    private final ParameterList querystringParams;
    private final ParameterList bodyParams;
    private final Map<String, String> headers;
    private String payload = null;
    private HttpURLConnection connection;
    private String charset;
    private byte[] bytePayload = null;
    private boolean connectionKeepAlive = false;
    private Long connectTimeout = null;
    private Long readTimeout = null;
    private String proxyHost = null;
    private int proxyPort = 8080;
    
    /**
     * Creates a new Http Request
     * 
     * @param verb Http Verb (GET, POST, etc)
     * @param url url with optional querystring parameters.
     */
    public ProxyRequest(final Verb verb, final String url) {
        this.verb = verb;
        this.url = url;
        this.querystringParams = new ParameterList();
        this.bodyParams = new ParameterList();
        this.headers = new HashMap<String, String>();
    }
    
    /**
     * Execute the request and return a {@link Response}
     * 
     * @return Http Response
     * @throws RuntimeException if the connection cannot be created.
     */
    public Response send() {
        try {
            createConnection();
            return doSend();
        } catch (final Exception e) {
            throw new OAuthConnectionException(e);
        }
    }
    
    private void createConnection() throws IOException {
        final String completeUrl = getCompleteUrl();
        if (this.connection == null) {
            if (this.connectionKeepAlive) {
                this.connection.setRequestProperty("Connection", "keep-alive");
            }
            if (StringUtils.isNotBlank(this.proxyHost)) {
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
                this.connection = (HttpURLConnection) new URL(completeUrl).openConnection(proxy);
            } else {
                this.connection = (HttpURLConnection) new URL(completeUrl).openConnection();
            }
        }
    }
    
    /**
     * Returns the complete url (host + resource + encoded querystring parameters).
     * 
     * @return the complete url.
     */
    public String getCompleteUrl() {
        return this.querystringParams.appendTo(this.url);
    }
    
    Response doSend() throws IOException {
        this.connection.setRequestMethod(this.verb.name());
        if (this.connectTimeout != null) {
            this.connection.setConnectTimeout(this.connectTimeout.intValue());
        }
        if (this.readTimeout != null) {
            this.connection.setReadTimeout(this.readTimeout.intValue());
        }
        addHeaders(this.connection);
        if (this.verb.equals(Verb.PUT) || this.verb.equals(Verb.POST)) {
            addBody(this.connection, getByteBodyContents());
        }
        return new Response(this.connection);
    }
    
    void addHeaders(final HttpURLConnection conn) {
        for (final String key : this.headers.keySet())
            conn.setRequestProperty(key, this.headers.get(key));
    }
    
    void addBody(final HttpURLConnection conn, final byte[] content) throws IOException {
        conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(content.length));
        
        // Set default content type if none is set.
        if (conn.getRequestProperty(CONTENT_TYPE) == null) {
            conn.setRequestProperty(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        }
        conn.setDoOutput(true);
        conn.getOutputStream().write(content);
    }
    
    /**
     * Add an HTTP Header to the Request
     * 
     * @param key the header name
     * @param value the header value
     */
    public void addHeader(final String key, final String value) {
        this.headers.put(key, value);
    }
    
    /**
     * Add a body Parameter (for POST/ PUT Requests)
     * 
     * @param key the parameter name
     * @param value the parameter value
     */
    public void addBodyParameter(final String key, final String value) {
        this.bodyParams.add(key, value);
    }
    
    /**
     * Add a QueryString parameter
     * 
     * @param key the parameter name
     * @param value the parameter value
     */
    public void addQuerystringParameter(final String key, final String value) {
        this.querystringParams.add(key, value);
    }
    
    /**
     * Add body payload. This method is used when the HTTP body is not a form-url-encoded string, but another thing. Like for example XML.
     * Note: The contents are not part of the OAuth signature
     * 
     * @param payload the body of the request
     */
    public void addPayload(final String payload) {
        this.payload = payload;
    }
    
    /**
     * Overloaded version for byte arrays
     * 
     * @param payload
     */
    public void addPayload(final byte[] payload) {
        this.bytePayload = payload;
    }
    
    /**
     * Get a {@link ParameterList} with the query string parameters.
     * 
     * @return a {@link ParameterList} containing the query string parameters.
     * @throws OAuthException if the request URL is not valid.
     */
    public ParameterList getQueryStringParams() {
        try {
            final ParameterList result = new ParameterList();
            final String queryString = new URL(this.url).getQuery();
            result.addQuerystring(queryString);
            result.addAll(this.querystringParams);
            return result;
        } catch (final MalformedURLException mue) {
            throw new OAuthException("Malformed URL", mue);
        }
    }
    
    /**
     * Obtains a {@link ParameterList} of the body parameters.
     * 
     * @return a {@link ParameterList}containing the body parameters.
     */
    public ParameterList getBodyParams() {
        return this.bodyParams;
    }
    
    /**
     * Obtains the URL of the HTTP Request.
     * 
     * @return the original URL of the HTTP Request
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * Returns the URL without the port and the query string part.
     * 
     * @return the OAuth-sanitized URL
     */
    public String getSanitizedUrl() {
        return this.url.replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
    }
    
    /**
     * Returns the body of the request
     * 
     * @return form encoded string
     * @throws OAuthException if the charset chosen is not supported
     */
    public String getBodyContents() {
        try {
            return new String(getByteBodyContents(), getCharset());
        } catch (final UnsupportedEncodingException uee) {
            throw new OAuthException("Unsupported Charset: " + this.charset, uee);
        }
    }
    
    byte[] getByteBodyContents() {
        if (this.bytePayload != null)
            return this.bytePayload;
        final String body = (this.payload != null) ? this.payload : this.bodyParams.asFormUrlEncodedString();
        try {
            return body.getBytes(getCharset());
        } catch (final UnsupportedEncodingException uee) {
            throw new OAuthException("Unsupported Charset: " + getCharset(), uee);
        }
    }
    
    /**
     * Returns the HTTP Verb
     * 
     * @return the verb
     */
    public Verb getVerb() {
        return this.verb;
    }
    
    /**
     * Returns the connection headers as a {@link Map}
     * 
     * @return map of headers
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }
    
    /**
     * Returns the connection charset. Defaults to {@link Charset} defaultCharset if not set
     * 
     * @return charset
     */
    public String getCharset() {
        return this.charset == null ? Charset.defaultCharset().name() : this.charset;
    }
    
    /**
     * Sets the connect timeout for the underlying {@link HttpURLConnection}
     * 
     * @param duration duration of the timeout
     * @param unit unit of time (milliseconds, seconds, etc)
     */
    public void setConnectTimeout(final int duration, final TimeUnit unit) {
        this.connectTimeout = unit.toMillis(duration);
    }
    
    /**
     * Sets the read timeout for the underlying {@link HttpURLConnection}
     * 
     * @param duration duration of the timeout
     * @param unit unit of time (milliseconds, seconds, etc)
     */
    public void setReadTimeout(final int duration, final TimeUnit unit) {
        this.readTimeout = unit.toMillis(duration);
    }
    
    /**
     * Set the charset of the body of the request
     * 
     * @param charsetName name of the charset of the request
     */
    public void setCharset(final String charsetName) {
        this.charset = charsetName;
    }
    
    /**
     * Sets whether the underlying Http Connection is persistent or not.
     * 
     * @see http://download.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
     * @param connectionKeepAlive
     */
    public void setConnectionKeepAlive(final boolean connectionKeepAlive) {
        this.connectionKeepAlive = connectionKeepAlive;
    }
    
    /*
     * We need this in order to stub the connection object for test cases
     */
    void setConnection(final HttpURLConnection connection) {
        this.connection = connection;
    }
    
    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    @Override
    public String toString() {
        return String.format("@ProxyRequest(%s %s)", getVerb(), getUrl());
    }
}
