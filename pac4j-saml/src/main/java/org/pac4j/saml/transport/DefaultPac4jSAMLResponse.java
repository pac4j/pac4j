package org.pac4j.saml.transport;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.context.WebContext;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Empty response adapter containing a {@link ByteArrayOutputStream} in order opensaml can write
 * the saml messages. The content can be retrieved as a String from getOutgoingContent().
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public class DefaultPac4jSAMLResponse implements Pac4jSAMLResponse {
    private final ByteArrayOutputStream outputStream;
    @Getter
    private final OutputStreamWriter outputStreamWriter;
    @Getter
    private final WebContext webContext;

    @Getter @Setter
    private String redirectUrl;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the response
     * @throws IllegalArgumentException if the response is null
     */
    public DefaultPac4jSAMLResponse(final WebContext response) {
        webContext = response;

        outputStream = new ByteArrayOutputStream();
        outputStreamWriter = new Pac4jServletOutputStreamWriter(outputStream);
    }

    /** {@inheritDoc} */
    @Override
    public final String getOutgoingContent() {
        return outputStreamWriter.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void init() {
        setNoCacheHeaders();
    }

    /**
     * <p>setNoCacheHeaders.</p>
     */
    public void setNoCacheHeaders() {
        webContext.setResponseHeader("Cache-control", "no-cache, no-store");
        webContext.setResponseHeader("Pragma", "no-cache");
    }

    /** {@inheritDoc} */
    @Override
    public void setContentType(final String type) {
        webContext.setResponseContentType(type + ";charset=" + StandardCharsets.UTF_8);
    }

    private static class Pac4jServletOutputStreamWriter extends OutputStreamWriter {
        private final ByteArrayOutputStream outputStream;
        public Pac4jServletOutputStreamWriter(final ByteArrayOutputStream out) {
            super(out, StandardCharsets.UTF_8);
            outputStream = out;
        }

        public final String getOutgoingContent() {
            try {
                val result = new String(this.outputStream.toByteArray(), StandardCharsets.UTF_8);
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
