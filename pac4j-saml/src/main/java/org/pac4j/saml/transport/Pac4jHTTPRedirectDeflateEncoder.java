package org.pac4j.saml.transport;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPRedirectDeflateEncoder;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pac4j implementation of the {@link HTTPRedirectDeflateEncoder}
 * that ignores the http request in favor of {@link WebContext}.
 * @author Misagh Moayyed
 * @since 1.8
 */
public class Pac4jHTTPRedirectDeflateEncoder extends HTTPRedirectDeflateEncoder {

    private final static Logger logger = LoggerFactory.getLogger(Pac4jHTTPPostEncoder.class);

    private final WebContext context;
    private final Pac4jSAMLResponse responseAdapter;

    public Pac4jHTTPRedirectDeflateEncoder(final WebContext context, final Pac4jSAMLResponse responseAdapter) {
        this.context = context;
        this.responseAdapter = responseAdapter;
    }

    @Override
    protected void doEncode() throws MessageEncodingException {
        final MessageContext messageContext = this.getMessageContext();
        final SAMLObject outboundMessage = (SAMLObject)messageContext.getMessage();
        final String endpointURL = this.getEndpointURL(messageContext).toString();
        this.removeSignature(outboundMessage);
        final String encodedMessage = this.deflateAndBase64Encode(outboundMessage);
        final String redirectURL = this.buildRedirectURL(messageContext, endpointURL, encodedMessage);

        responseAdapter.init();
        responseAdapter.setRedirectUrl(redirectURL);
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        logger.debug("Initialized {}", this.getClass().getSimpleName());
    }
}
