package org.pac4j.saml.transport;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.SAML2MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Common decoder.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractPac4jDecoder extends AbstractMessageDecoder<SAMLObject> {

    static final String[] SAML_PARAMETERS = {"SAMLRequest", "SAMLResponse", "logoutRequest"};

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Parser pool used to deserialize the message. */
    protected ParserPool parserPool;

    protected final WebContext context;

    public AbstractPac4jDecoder(final WebContext context) {
        CommonHelper.assertNotNull("context", context);
        this.context = context;
    }

    protected byte[] getBase64DecodedMessage() throws MessageDecodingException {
        Optional<String> encodedMessage = Optional.empty();
        for (final String parameter : SAML_PARAMETERS) {
            encodedMessage = this.context.getRequestParameter(parameter);
            if (encodedMessage.isPresent()) {
                break;
            }
        }
        if (!encodedMessage.isPresent()) {
            encodedMessage = Optional.ofNullable(this.context.getRequestContent());
        }

        if (!encodedMessage.isPresent()) {
            throw new MessageDecodingException("Request did not contain either a SAMLRequest parameter, a SAMLResponse parameter, "
                + "a logoutRequest parameter or a body content");
        } else {
            List<NameValuePair> a = URLEncodedUtils.parse(
                    encodedMessage.get(),
                    StandardCharsets.UTF_8);
            Multimap<String, String>  paramMap = HashMultimap.create();
            for (NameValuePair p : a) {
                paramMap.put(p.getName(), p.getValue());
            }
            for (String parameter : SAML_PARAMETERS) {
                Collection<String> newEncodedMessageCollection = paramMap.get(parameter);
                if (newEncodedMessageCollection != null && !newEncodedMessageCollection.isEmpty()) {
                    encodedMessage = Optional.of(newEncodedMessageCollection.iterator().next());
                    break;
                }                
            }

            if (encodedMessage.get().contains("<")) {
                logger.trace("Raw SAML message:\n{}", encodedMessage);
                return encodedMessage.get().getBytes(StandardCharsets.UTF_8);
            } else {
                final byte[] decodedBytes = Base64Support.decode(encodedMessage.get());
                logger.trace("Decoded SAML message:\n{}", new String(decodedBytes, StandardCharsets.UTF_8));
                return decodedBytes;
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
    protected void populateBindingContext(final SAML2MessageContext messageContext) {
        SAMLBindingContext bindingContext = messageContext.getSubcontext(SAMLBindingContext.class, true);
        bindingContext.setBindingUri(getBindingURI(messageContext));
        bindingContext.setHasBindingSignature(false);
        bindingContext.setIntendedDestinationEndpointURIRequired(SAMLBindingSupport.isMessageSigned(messageContext));
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

