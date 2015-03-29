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
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a Scribe {@link org.scribe.model.OAuthRequest} with proxy capabilities. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @version 1.2.0
 */
public class ProxyOAuthRequest extends OAuthRequest {
    
    private final ProxyRequest proxyRequest;
    
    public ProxyOAuthRequest(final Verb verb, final String url, final int connectTimeout, final int readTimeout,
                             final String proxyHost, final int proxyPort) {
        super(verb, url);
        this.proxyRequest = new ProxyRequest(verb, url);
        if (connectTimeout != 0) {
            this.proxyRequest.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }
        if (readTimeout != 0) {
            this.proxyRequest.setReadTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        this.proxyRequest.setProxyHost(proxyHost);
        this.proxyRequest.setProxyPort(proxyPort);
    }
    
    @Override
    public Response send() {
        return this.proxyRequest.send();
    }
    
    @Override
    public String getCompleteUrl() {
        return this.proxyRequest.getCompleteUrl();
    }
    
    @Override
    void addHeaders(final HttpURLConnection conn) {
        this.proxyRequest.addHeaders(conn);
    }
    
    @Override
    void addBody(final HttpURLConnection conn, final byte[] content) throws IOException {
        this.proxyRequest.addBody(conn, content);
    }
    
    @Override
    public void addHeader(final String key, final String value) {
        this.proxyRequest.addHeader(key, value);
    }
    
    @Override
    public void addBodyParameter(final String key, final String value) {
        this.proxyRequest.addBodyParameter(key, value);
    }
    
    @Override
    public void addQuerystringParameter(final String key, final String value) {
        this.proxyRequest.addQuerystringParameter(key, value);
    }
    
    @Override
    public void addPayload(final String payload) {
        this.proxyRequest.addPayload(payload);
    }
    
    @Override
    public void addPayload(final byte[] payload) {
        this.proxyRequest.addPayload(payload);
    }
    
    @Override
    public ParameterList getQueryStringParams() {
        return this.proxyRequest.getQueryStringParams();
    }
    
    @Override
    public ParameterList getBodyParams() {
        return this.proxyRequest.getBodyParams();
    }
    
    @Override
    public String getUrl() {
        return this.proxyRequest.getUrl();
    }
    
    @Override
    public String getSanitizedUrl() {
        return this.proxyRequest.getSanitizedUrl();
    }
    
    @Override
    public String getBodyContents() {
        return this.proxyRequest.getBodyContents();
    }
    
    @Override
    byte[] getByteBodyContents() {
        return this.proxyRequest.getByteBodyContents();
    }
    
    @Override
    public Verb getVerb() {
        return this.proxyRequest.getVerb();
    }
    
    @Override
    public Map<String, String> getHeaders() {
        return this.proxyRequest.getHeaders();
    }
    
    @Override
    public String getCharset() {
        return this.proxyRequest.getCharset();
    }
    
    @Override
    public void setCharset(final String charsetName) {
        this.proxyRequest.setCharset(charsetName);
    }
    
    @Override
    public void setConnectionKeepAlive(final boolean connectionKeepAlive) {
        this.proxyRequest.setConnectionKeepAlive(connectionKeepAlive);
    }
    
    @Override
    public String toString() {
        return String.format("@ProxyOAuthRequest(%s %s)", getVerb(), getUrl());
    }
}
