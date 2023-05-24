package org.pac4j.saml.transport;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.XMLParserException;
import org.apache.hc.core5.net.URLEncodedUtils;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.pac4j.core.context.CallContext;
import org.pac4j.saml.context.SAML2MessageContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Common decoder.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPac4jDecoder extends AbstractMessageDecoder {

    static final String[] SAML_PARAMETERS = {"SAMLRequest", "SAMLResponse", "logoutRequest"};

    /** Parser pool used to deserialize the message. */
    @Getter
    protected ParserPool parserPool;

    @Getter
    protected final CallContext callContext;


    /**
     * <p>getBase64DecodedMessage.</p>
     *
     * @return an array of {@link byte} objects
     * @throws MessageDecodingException if any.
     */
    protected byte[] getBase64DecodedMessage() throws MessageDecodingException {
        Optional<String> encodedMessage = Optional.empty();
        for (val parameter : SAML_PARAMETERS) {
            encodedMessage = this.callContext.webContext().getRequestParameter(parameter);
            if (encodedMessage.isPresent()) {
                break;
            }
        }
        if (encodedMessage.isEmpty()) {
            encodedMessage = Optional.ofNullable(this.callContext.webContext().getRequestContent());
            // we have a body, it may be the SAML request/response directly
            // but we also try to parse it as a list key=value where the value is the SAML request/response
            if (encodedMessage.isPresent()) {
                val a = URLEncodedUtils.parse(encodedMessage.get(), StandardCharsets.UTF_8);
                final Multimap<String, String>  paramMap = HashMultimap.create();
                for (val p : a) {
                    paramMap.put(p.getName(), p.getValue());
                }
                for (val parameter : SAML_PARAMETERS) {
                    val newEncodedMessageCollection = paramMap.get(parameter);
                    if (newEncodedMessageCollection != null && !newEncodedMessageCollection.isEmpty()) {
                        encodedMessage = Optional.of(newEncodedMessageCollection.iterator().next());
                        break;
                    }
                }
            }
        }

        if (encodedMessage.isEmpty()) {
            throw new MessageDecodingException("Request did not contain either a SAMLRequest parameter, a SAMLResponse parameter, "
                + "a logoutRequest parameter or a body content");
        } else {
            if (encodedMessage.get().contains("<")) {
                LOGGER.trace("Raw SAML message:\n{}", encodedMessage);
                return encodedMessage.get().getBytes(StandardCharsets.UTF_8);
            } else {

                try {
                    val decodedBytes = Base64Support.decode(encodedMessage.get());
                    LOGGER.trace("Decoded SAML message:\n{}", new String(decodedBytes, StandardCharsets.UTF_8));
                    return decodedBytes;
                } catch (final Exception e) {
                    throw new MessageDecodingException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        parserPool = null;

        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        LOGGER.debug("Initialized {}", this.getClass().getSimpleName());

        if (parserPool == null) {
            throw new ComponentInitializationException("Parser pool cannot be null");
        }
    }

    /**
     * Populate the context which carries information specific to this binding.
     *
     * @param messageContext the current message context
     */
    protected void populateBindingContext(final SAML2MessageContext messageContext) {
        val bindingContext = messageContext.getMessageContext().getSubcontext(SAMLBindingContext.class, true);
        bindingContext.setBindingUri(getBindingURI(messageContext));
        bindingContext.setHasBindingSignature(false);
        bindingContext.setRelayState(bindingContext.getRelayState());
        bindingContext.setIntendedDestinationEndpointURIRequired(SAMLBindingSupport.isMessageSigned(messageContext.getMessageContext()));
    }

    /**
     * Get the binding of the message context;.
     *
     * @param messageContext the message context
     * @return the binding URI
     */
    public abstract String getBindingURI(SAML2MessageContext messageContext);

    /**
     * Helper method that deserializes and unmarshalls the message from the given stream.
     *
     * @param messageStream input stream containing the message
     * @return the inbound message
     * @throws MessageDecodingException thrown if there is a problem deserializing/unmarshalling the message
     */
    protected XMLObject unmarshallMessage(final InputStream messageStream) throws MessageDecodingException {
        try {
            val message = XMLObjectSupport.unmarshallFromInputStream(getParserPool(), messageStream);
            return message;
        } catch (final XMLParserException e) {
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        } catch (final UnmarshallingException e) {
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        }
    }

    /**
     * Sets the parser pool used to deserialize incoming messages.
     *
     * @param pool parser pool used to deserialize incoming messages
     */
    public void setParserPool(final ParserPool pool) {
        Constraint.isNotNull(pool, "ParserPool cannot be null");
        parserPool = pool;
    }
}

