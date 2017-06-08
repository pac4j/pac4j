package org.pac4j.http.test.tools;

import fi.iki.elonen.NanoHTTPD;
import org.pac4j.core.exception.TechnicalException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A web server for tests.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class WebServer extends NanoHTTPD {

    private final Map<String, NanoResponse> responses;

    public WebServer(final int port, final Map<String, NanoResponse> responses) {
        super(port);
        this.responses = responses;
    }

    public void start() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (final IOException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String r = null;
        final List<String> parameterList = session.getParameters().get("r");
        if (parameterList != null && parameterList.size() > 0) {
            r = parameterList.get(0);
        }
        final NanoResponse response = responses.get(r);
        if (response != null) {
            return newFixedLengthResponse(response.getStatus(), response.getMimeType(), response.getBody());
        } else {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.SERVICE_UNAVAILABLE, "plain/text", "no response available");
        }
    }
}
