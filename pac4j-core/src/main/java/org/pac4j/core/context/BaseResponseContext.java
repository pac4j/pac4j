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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * This class implements the methods related to the response as a POJO.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseResponseContext implements WebContext {

    protected String responseContent = "";

    protected int responseStatus = -1;

    protected String responseLocation;

    protected String responseContentType;

    protected String responseEncoding;

    protected final Map<String, String> responseHeaders = new HashMap<>();

    protected final Collection<Cookie> responseCookies = new LinkedHashSet<>();


    public void writeResponseContent(final String content) {
        if (content != null) {
            this.responseContent += content;
        }
    }

    public void setResponseStatus(final int code) {
        this.responseStatus = code;
    }

    public void setResponseHeader(final String name, final String value) {
        this.responseHeaders.put(name, value);
    }

    public String getResponseContent() {
        return this.responseContent;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    public String getResponseLocation() {
        return this.responseHeaders.get(HttpConstants.LOCATION_HEADER);
    }

    public Map<String, String> getResponseHeaders() {
        return this.responseHeaders;
    }

    @Override
    public void setResponseCharacterEncoding(final String encoding) {
        this.responseEncoding = encoding;
    }

    @Override
    public void setResponseContentType(final String content) {
        this.responseContentType = content;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        this.responseCookies.add(cookie);
    }
}
