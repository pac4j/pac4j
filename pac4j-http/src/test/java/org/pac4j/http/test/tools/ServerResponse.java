package org.pac4j.http.test.tools;

import fi.iki.elonen.NanoHTTPD;

/**
 * A server response.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class ServerResponse {

    private final NanoHTTPD.Response.IStatus status;

    private final String mimeType;

    private final String body;

    public ServerResponse(final NanoHTTPD.Response.IStatus status, final String mimeType, final String body) {
        this.status = status;
        this.mimeType = mimeType;
        this.body = body;
    }

    public NanoHTTPD.Response.IStatus getStatus() {
        return status;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getBody() {
        return body;
    }
}
