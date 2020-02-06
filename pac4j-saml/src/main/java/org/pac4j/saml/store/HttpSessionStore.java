package org.pac4j.saml.store;

import java.io.StringReader;
import org.opensaml.core.xml.XMLObject;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Optional;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.pac4j.saml.util.Configuration;

/**
 * Class implements store of SAML messages and uses HttpSession as underlying dataStore. As the XMLObjects
 * can't be serialized (which could lead to problems during failover), the messages are transformed into SAMLObject
 * which internally marshalls the content into XML during serialization.
 *
 * Messages are populated to a Hashtable and stored inside HttpSession. The Hashtable is lazily initialized
 * during first attempt to create or retrieve a message.
 *
 * @author Vladimir Schäfer
 */
public class HttpSessionStore implements SAMLMessageStore {

    /**
     * Class logger.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The web context to store data.
     */
    private final WebContext context;

    /**
     * Internal store for messages, corresponding to the object in session.
     */
    private LinkedHashMap<String, String> internalMessages;

    /**
     * Session key for storing the hashtable.
     */
    private static final String SAML_STORAGE_KEY = "_springSamlStorageKey";

    /**
     * Creates the store object. The session is manipulated only once caller tries to store
     * or retrieve a message.
     *
     * In case request doesn't already have a started session, it will be created.
     *
     * @param context context to load/store internalMessages from
     */
    public HttpSessionStore(final WebContext context) {
        CommonHelper.assertNotNull("context", context);
        this.context = context;
    }


    /**
     * Stores a request message into the repository. RequestAbstractType must have an ID
     * set. Any previous message with the same ID will be overwritten.
     *
     * @param messageID ID of message
     * @param message   message to be stored
     */
    @Override
    public void set(final String messageID, final XMLObject message) {
        log.debug("Storing message {} to session {}", messageID, context.getSessionStore().getOrCreateSessionId(context));
        final LinkedHashMap<String, String> messages = getMessages();
        messages.put(messageID, Configuration.serializeSamlObject(message).toString());
        updateSession(messages);
    }

    /**
     * Returns previously stored message with the given ID or null, if there is no message
     * stored.
     * <p>
     * Message is stored in String format and must be unmarshalled into XMLObject. Call to this
     * method may thus be expensive.
     * <p>
     * Messages are automatically cleared upon successful reception, as we presume that there
     * are never multiple ongoing SAML exchanges for the same session. This saves memory used by
     * the session.
     *
     * @param messageID ID of message to retrieve
     * @return message found or null
     */
    @Override
    public Optional<XMLObject> get(final String messageID) {
        final LinkedHashMap<String, String> messages = getMessages();
        final String o = messages.get(messageID);
        if (o == null) {
            log.debug("Message {} not found in session {}",
                    messageID, context.getSessionStore().getOrCreateSessionId(context));
            return Optional.empty();
        }

        log.debug("Message {} found in session {}, clearing",
                messageID, context.getSessionStore().getOrCreateSessionId(context));
        messages.clear();
        updateSession(messages);

        try {
            XMLObject message = XMLObjectSupport.unmarshallFromReader(
                    Configuration.getParserPool(), new StringReader(o));
            return Optional.of(message);
        } catch (Exception e) {
            log.error("Error unmarshalling message from input stream", e);
            return Optional.empty();
        }
    }

    /**
     * Provides message store hashtable. Table is lazily initialized when user tries to store or retrieve
     * the first message.
     *
     * @return message store
     */
    private LinkedHashMap<String, String> getMessages() {
        if (internalMessages == null) {
            internalMessages = initializeSession();
        }
        return internalMessages;
    }

    /**
     * Call to the method tries to load internalMessages hashtable object from the session, if the object doesn't exist
     * it will be created and stored.
     * <p>
     * Method synchronizes on session object to prevent two threads from overwriting each others hashtable.
     */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, String> initializeSession() {
        Optional<LinkedHashMap<String, String>> messages = (Optional<LinkedHashMap<String, String>>)
                context.getSessionStore().get(context, SAML_STORAGE_KEY);
        if (!messages.isPresent()) {
            synchronized (context) {
                messages = (Optional<LinkedHashMap<String, String>>) context.
                        getSessionStore().get(context, SAML_STORAGE_KEY);
                if (!messages.isPresent()) {
                    messages = Optional.of(new LinkedHashMap<>());
                    updateSession(messages.get());
                }
            }
        }
        return messages.get();
    }

    /**
     * Updates session with the internalMessages key. Some application servers require session value to be updated
     * in order to replicate the session across nodes or persist it correctly.
     */
    private void updateSession(final LinkedHashMap<String, String> messages) {
        context.getSessionStore().set(context, SAML_STORAGE_KEY, messages);
    }

    @Override
    public void remove(final String key) {
        set(key, null);
    }
}
