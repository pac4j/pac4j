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

    protected String responseContentType;

    protected final Map<String, String> responseHeaders = new HashMap<>();

    protected final Collection<Cookie> responseCookies = new LinkedHashSet<>();

    @Override
    public void writeResponseContent(final String content) {
        if (content != null) {
            this.responseContent += content;
        }
    }

    @Override
    public void setResponseStatus(final int code) {
        this.responseStatus = code;
    }

    @Override
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
    public void setResponseContentType(final String content) {
        this.responseContentType = content;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        this.responseCookies.add(cookie);
    }
}
