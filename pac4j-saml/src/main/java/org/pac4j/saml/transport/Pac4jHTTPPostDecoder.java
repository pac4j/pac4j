package org.pac4j.saml.transport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.impl.SAMLSOAPDecoderBodyHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.soap.soap11.Envelope;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.context.SAML2MessageContext;

import java.io.ByteArrayInputStream;

/**
 * Decoder for messages sent via POST and SOAP bindings.
 *
 * @author Misagh Moayyed
 * @author Jerome Leleu
 * @since 1.8
 */
public class Pac4jHTTPPostDecoder extends AbstractPac4jDecoder {

    public Pac4jHTTPPostDecoder(final WebContext context) {
        super(context);
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        final SAML2MessageContext messageContext = new SAML2MessageContext();

        if (ContextHelper.isPost(context)) {
            final String relayState = this.context.getRequestParameter("RelayState");
            logger.debug("Decoded SAML relay state of: {}", relayState);
            SAMLBindingSupport.setRelayState(messageContext, relayState);
            final byte[] base64DecodedMessage = this.getBase64DecodedMessage();
            final XMLObject xmlObject = this.unmarshallMessage(new ByteArrayInputStream(base64DecodedMessage));
            final SAMLObject inboundMessage;
            if (xmlObject instanceof Envelope) {
                Envelope soapMessage = (Envelope) xmlObject;
                messageContext.getSOAP11Context().setEnvelope(soapMessage);
                try {
                    new SAMLSOAPDecoderBodyHandler().invoke(messageContext);
                } catch (final MessageHandlerException e) {
                    throw new MessageDecodingException("Cannot decode SOAP envelope", e);
                }
            } else {
                inboundMessage = (SAMLObject) xmlObject;
                messageContext.setMessage(inboundMessage);
            }
            logger.debug("Decoded SAML message");
            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext);

        } else {
            throw new MessageDecodingException("This message decoder only supports the HTTP POST method");
        }
    }

    @Override
    public String getBindingURI(final SAML2MessageContext messageContext) {
        if (messageContext.getSOAP11Context().getEnvelope() != null) {
            return SAMLConstants.SAML2_SOAP11_BINDING_URI;
        } else {
            return SAMLConstants.SAML2_POST_BINDING_URI;
        }
    }
}
