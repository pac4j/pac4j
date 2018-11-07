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
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Common decoder.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractPac4jDecoder extends AbstractMessageDecoder<SAMLObject> {

    static final String[] SAML_PARAMETERS = {"SAMLRequest", "SAMLResponse", "logoutRequest"};

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final boolean cas5Compatibility;

    /** Parser pool used to deserialize the message. */
    protected ParserPool parserPool;

    protected final WebContext context;

    public AbstractPac4jDecoder(final WebContext context, final boolean cas5Compatibility) {
        CommonHelper.assertNotNull("context", context);
        this.context = context;
        this.cas5Compatibility = cas5Compatibility;
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
            throw new MessageDecodingException("Request did not contain either a SAMLRequest, a SAMLResponse or a logoutRequest parameter");
        } else {
            if (encodedMessage.contains("<") && cas5Compatibility) {
                logger.warn("Not a base64 message: using it for CAS v5 backward compatibility");
                logger.trace("Non-decoded SAML message:\n{}", encodedMessage);
                return new ByteArrayInputStream(encodedMessage.getBytes(StandardCharsets.UTF_8));
            } else {
                final byte[] decodedBytes = Base64Support.decode(encodedMessage);
                final String decodedMessage = new String(decodedBytes, StandardCharsets.UTF_8);
                logger.trace("Decoded SAML message:\n{}", decodedMessage);
                return new ByteArrayInputStream(decodedBytes);
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

    public abstract String getBindingURI();

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

