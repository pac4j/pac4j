package org.pac4j.saml.transport;

import com.google.common.base.Strings;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Pac4j implementation extending directly the {@link AbstractMessageDecoder} as intermediate classes use the J2E HTTP request.
 * It's mostly a copy/paste of the source code of these intermediate opensaml classes.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public class Pac4jHTTPPostDecoder extends AbstractMessageDecoder<SAMLObject> {
    private static final Logger logger = LoggerFactory.getLogger(Pac4jHTTPPostDecoder.class);

    private static final String[] SAML_PARAMETERS = {"SAMLRequest", "SAMLResponse", "logoutRequest"};

    /** Parser pool used to deserialize the message. */
    private ParserPool parserPool;

    private final WebContext context;

    public Pac4jHTTPPostDecoder(final WebContext context) {
        CommonHelper.assertNotNull("context", context);
        this.context = context;
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        final MessageContext messageContext = new MessageContext();

        if(!HttpConstants.HTTP_METHOD.POST.name().equalsIgnoreCase(this.context.getRequestMethod())) {
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

    protected InputStream getBase64DecodedMessage() throws MessageDecodingException {
        String encodedMessage = null;
        for (final String parameter : SAML_PARAMETERS) {
            encodedMessage = this.context.getRequestParameter(parameter);
            if (CommonHelper.isNotBlank(encodedMessage)) {
                break;
            }
        }

        if (Strings.isNullOrEmpty(encodedMessage)) {
            throw new MessageDecodingException("Request did not contain either a SAMLRequest, a SAMLResponse or a logoutRequest parameter. "
                + "Invalid request for SAML 2 HTTP POST binding.");
        } else {
            logger.trace("Base64 decoding SAML message:\n{}", encodedMessage);
            final byte[] decodedBytes = Base64Support.decode(encodedMessage);
            final String decodedMessage = new String(decodedBytes, StandardCharsets.UTF_8);
            if (decodedMessage.contains("<")) {
                logger.trace("Decoded SAML message:\n{}", decodedMessage);
                return new ByteArrayInputStream(decodedBytes);
            } else {
                logger.warn("Unable to Base64 decode SAML message: using it as is (likely a CAS v<6 message)");
                return new ByteArrayInputStream(encodedMessage.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    @Override
    protected void doDestroy() {
        parserPool = null;

        super.doDestroy();
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        logger.debug("Initialized {}", this.getClass().getSimpleName());

        if (parserPool == null) {
            throw new ComponentInitializationException("Parser pool cannot be null");
        }
    }

    /**
     * Populate the context which carries information specific to this binding.
     *
     * @param messageContext the current message context
     */
    protected void populateBindingContext(MessageContext<SAMLObject> messageContext) {
        SAMLBindingContext bindingContext = messageContext.getSubcontext(SAMLBindingContext.class, true);
        bindingContext.setBindingUri(getBindingURI());
        bindingContext.setHasBindingSignature(false);
        bindingContext.setIntendedDestinationEndpointURIRequired(SAMLBindingSupport.isMessageSigned(messageContext));
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    /**
     * Helper method that deserializes and unmarshalls the message from the given stream.
     *
     * @param messageStream input stream containing the message
     *
     * @return the inbound message
     *
     * @throws MessageDecodingException thrown if there is a problem deserializing and unmarshalling the message
     */
    protected XMLObject unmarshallMessage(InputStream messageStream) throws MessageDecodingException {
        try {
            XMLObject message = XMLObjectSupport.unmarshallFromInputStream(getParserPool(), messageStream);
            return message;
        } catch (XMLParserException e) {
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        } catch (UnmarshallingException e) {
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        }
    }

    /**
     * Gets the parser pool used to deserialize incoming messages.
     *
     * @return parser pool used to deserialize incoming messages
     */
    @Nonnull
    public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to deserialize incoming messages.
     *
     * @param pool parser pool used to deserialize incoming messages
     */
    public void setParserPool(@Nonnull final ParserPool pool) {
        Constraint.isNotNull(pool, "ParserPool cannot be null");
        parserPool = pool;
    }
}
