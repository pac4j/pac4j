package org.pac4j.saml.transport;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Empty response adapter containing a {@link ByteArrayOutputStream} in order opensaml can write
 * the saml messages. The content can be retrieved as a String from getOutgoingContent().
 * 
 * @author Misagh Moayyed
 * @since 1.8
 */
public class DefaultPac4jSAMLResponse implements Pac4jSAMLResponse {
    private final ByteArrayOutputStream outputStream;
    private final OutputStreamWriter outputStreamWriter;
    private final WebContext webContext;
    private String redirectUrl;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the response
     * @throws IllegalArgumentException if the response is null
     */
    public DefaultPac4jSAMLResponse(final WebContext response) {
        webContext = response;

        try {
            outputStream = new ByteArrayOutputStream();
            outputStreamWriter = new Pac4jServletOutputStreamWriter(outputStream);
        } catch (UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public final String getOutgoingContent() {
        return outputStreamWriter.toString();
    }

    @Override
    public WebContext getWebContext() {
        return webContext;
    }

    @Override
    public void init() {
        setNoCacheHeaders();
    }

    @Override
    public OutputStreamWriter getOutputStreamWriter() {
        return this.outputStreamWriter;
    }

    public void setNoCacheHeaders() {
        webContext.setResponseHeader("Cache-control", "no-cache, no-store");
        webContext.setResponseHeader("Pragma", "no-cache");
    }

    @Override
    public void setContentType(final String type) {
        webContext.setResponseContentType(type + ";charset=" + HttpConstants.UTF8_ENCODING);
    }

    @Override
    public final void setRedirectUrl(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    private static class Pac4jServletOutputStreamWriter extends OutputStreamWriter {
        private final ByteArrayOutputStream outputStream;
        public Pac4jServletOutputStreamWriter(ByteArrayOutputStream out) throws UnsupportedEncodingException {
            super(out, HttpConstants.UTF8_ENCODING);
            outputStream = out;
        }

        public final String getOutgoingContent() {
            try {
                final String result = new String(this.outputStream.toByteArray(), HttpConstants.UTF8_ENCODING);
                return result;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public String toString() {
            return this.getOutgoingContent();
        }
    }
}
