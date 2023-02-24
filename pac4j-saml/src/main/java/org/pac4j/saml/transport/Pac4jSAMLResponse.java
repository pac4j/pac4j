package org.pac4j.saml.transport;

import org.pac4j.core.context.WebContext;

import java.io.OutputStreamWriter;

/**
 * Indicates the SAML response that will be written
 * to the actual backend response via a given SAML encoder.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public interface Pac4jSAMLResponse {

    /**
     * Gets web context.
     *
     * @return the web context
     */
    WebContext getWebContext();

    /**
     * Initialize the response handling.
     */
    void init();

    /**
     * Gets output stream writer.
     *
     * @return the output stream writer
     */
    OutputStreamWriter getOutputStreamWriter();

    /**
     * Gets outgoing content.
     *
     * @return the outgoing content
     */
    String getOutgoingContent();

    /**
     * Sets redirect url.
     *
     * @param url the url
     */
    void setRedirectUrl(String url);

    /**
     * Gets redirect url.
     *
     * @return the redirect url
     */
    String getRedirectUrl();

    /**
     * Sets content type.
     *
     * @param type the type
     */
    void setContentType(String type);
}
