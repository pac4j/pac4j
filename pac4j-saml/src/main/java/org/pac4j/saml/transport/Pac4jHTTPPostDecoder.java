package org.pac4j.saml.transport;

import lombok.val;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.impl.SAMLSOAPDecoderBodyHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.soap.soap11.Envelope;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.util.SAML2Utils;

import java.io.ByteArrayInputStream;

/**
 * Decoder for messages sent via POST and SOAP bindings.
 *
 * @author Misagh Moayyed
 * @author Jerome Leleu
 * @since 1.8
 */
public class Pac4jHTTPPostDecoder extends AbstractPac4jDecoder {

    /**
     * <p>Constructor for Pac4jHTTPPostDecoder.</p>
     *
     * @param context a {@link org.pac4j.core.context.CallContext} object
     */
    public Pac4jHTTPPostDecoder(final CallContext context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    protected void doDecode() throws MessageDecodingException {
        val messageContext = new SAML2MessageContext(callContext);

        if (WebContextHelper.isPost(callContext.webContext())) {
            val relayState = this.callContext.webContext().getRequestParameter("RelayState").orElse(null);
            logger.debug("Decoded SAML relay state of: {}", relayState);
            SAMLBindingSupport.setRelayState(messageContext.getMessageContext(), relayState);
            val base64DecodedMessage = this.getBase64DecodedMessage();
            val xmlObject = this.unmarshallMessage(new ByteArrayInputStream(base64DecodedMessage));
            SAML2Utils.logProtocolMessage(xmlObject);
            final SAMLObject inboundMessage;
            if (xmlObject instanceof Envelope) {
                val soapMessage = (Envelope) xmlObject;
                messageContext.getSOAP11Context().setEnvelope(soapMessage);
                try {
                    new SAMLSOAPDecoderBodyHandler().invoke(messageContext.getMessageContext());
                } catch (final MessageHandlerException e) {
                    throw new MessageDecodingException("Cannot decode SOAP envelope", e);
                }
            } else {
                inboundMessage = (SAMLObject) xmlObject;
                messageContext.getMessageContext().setMessage(inboundMessage);
            }
            logger.debug("Decoded SAML message");
            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext.getMessageContext());

        } else {
            throw new MessageDecodingException("This message decoder only supports the HTTP POST method");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getBindingURI(final SAML2MessageContext messageContext) {
        if (messageContext.getSOAP11Context().getEnvelope() != null) {
            return SAMLConstants.SAML2_SOAP11_BINDING_URI;
        } else {
            return SAMLConstants.SAML2_POST_BINDING_URI;
        }
    }
}
