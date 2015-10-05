package org.pac4j.saml.transport;

import com.google.common.base.Strings;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPPostDecoder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Custom decoder for Pac4j that ignores the HttpServletRequest
 * in favor of {@link org.pac4j.core.context.WebContext}.
 * @author Misagh Moayyed
 * @since 1.8
 */
public class Pac4jHTTPPostDecoder extends HTTPPostDecoder {
    private final static Logger logger = LoggerFactory.getLogger(Pac4jHTTPPostDecoder.class);

    private final WebContext context;

    public Pac4jHTTPPostDecoder(final WebContext context) {
        this.context = context;
        if (this.context == null) {
            throw new TechnicalException("Context cannot be null");
        }
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        final MessageContext messageContext = new MessageContext();

        if(!"POST".equalsIgnoreCase(this.context.getRequestMethod())) {
            throw new MessageDecodingException("This message decoder only supports the HTTP POST method");
        } else {
            final String relayState = this.context.getRequestParameter("RelayState");
            logger.debug("Decoded SAML relay state of: {}", relayState);
            SAMLBindingSupport.setRelayState(messageContext, relayState);
            final InputStream base64DecodedMessage = this.getBase64DecodedMessage();
            final SAMLObject inboundMessage = (SAMLObject)this.unmarshallMessage(base64DecodedMessage);
            messageContext.setMessage(inboundMessage);
            logger.debug("Decoded SAML message");
            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext);
        }
    }

    protected InputStream getBase64DecodedMessage()
            throws MessageDecodingException {
        logger.debug("Getting Base64 encoded message from context, ignoring the given request");
        String encodedMessage = this.context.getRequestParameter("SAMLRequest");
        if(Strings.isNullOrEmpty(encodedMessage)) {
            encodedMessage = this.context.getRequestParameter("SAMLResponse");
        }

        if(Strings.isNullOrEmpty(encodedMessage)) {
            logger.error("Request did not contain either a SAMLRequest or SAMLResponse parameter. Invalid request for SAML 2 HTTP POST binding.");
            throw new MessageDecodingException("No SAML message present in request");
        } else {
            logger.trace("Base64 decoding SAML message:\n{}", encodedMessage);
            final byte[] decodedBytes = Base64Support.decode(encodedMessage);
            if(decodedBytes == null) {
                logger.error("Unable to Base64 decode SAML message");
                throw new MessageDecodingException("Unable to Base64 decode SAML message");
            } else {
                logger.trace("Decoded SAML message:\n{}", new String(decodedBytes));
                return new ByteArrayInputStream(decodedBytes);
            }
        }
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        logger.debug("Initialized {}", this.getClass().getSimpleName());

    }
}
