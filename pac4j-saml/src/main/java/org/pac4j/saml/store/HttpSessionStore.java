package org.pac4j.saml.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.opensaml.core.xml.XMLObject;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.saml.util.Configuration;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Class implements store of SAML messages and uses HttpSession as underlying dataStore. As the XMLObjects
 * can't be serialized (which could lead to problems during failover), the messages are transformed into SAMLObject
 * which internally marshals the content into XML during serialization.
 *
 * Messages are populated to a Hashtable and stored inside HttpSession. The Hashtable is lazily initialized
 * during first attempt to create or retrieve a message.
 *
 * @author Vladimir Schäfer
 */
@Slf4j
@RequiredArgsConstructor
public class HttpSessionStore implements SAMLMessageStore {

    /**
     * The web context to store data.
     */
    private final WebContext context;

    private final SessionStore sessionStore;

    /**
     * Internal store for messages, corresponding to the object in session.
     */
    private LinkedHashMap<String, String> internalMessages;

    /**
     * Session key for storing the hashtable.
     */
    private static final String SAML_STORAGE_KEY = "_springSamlStorageKey";


    /**
     * {@inheritDoc}
     *
     * Stores a request message into the repository. RequestAbstractType must have an ID
     * set. Any previous message with the same ID will be overwritten.
     */
    @Override
    public void set(final String messageID, final XMLObject message) {
        LOGGER.debug("Storing message {} to session {}", messageID,
            sessionStore.getSessionId(context, true).orElseThrow());
        val messages = getMessages();
        messages.put(messageID, Configuration.serializeSamlObject(message).toString());
        updateSession(messages);
    }

    /**
     * {@inheritDoc}
     *
     * Returns previously stored message with the given ID or null, if there is no message
     * stored.
     * <p>
     * Message is stored in String format and must be unmarshalled into XMLObject. Call to this
     * method may thus be expensive.
     * <p>
     * Messages are automatically cleared upon successful reception, as we presume that there
     * are never multiple ongoing SAML exchanges for the same session. This saves memory used by
     * the session.
     */
    @Override
    public Optional<XMLObject> get(final String messageID) {
        val messages = getMessages();
        val o = messages.get(messageID);
        if (o == null) {
            LOGGER.debug("Message {} not found in session {}", messageID, sessionStore.getSessionId(context, true).orElseThrow());
            return Optional.empty();
        }

        LOGGER.debug("Message {} found in session {}, clearing", messageID, sessionStore.getSessionId(context, true).orElseThrow());
        messages.clear();
        updateSession(messages);

        return Configuration.deserializeSamlObject(o);
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
        var messages = sessionStore.get(context, SAML_STORAGE_KEY);
        if (messages.isEmpty()) {
            synchronized (context) {
                messages = sessionStore.get(context, SAML_STORAGE_KEY);
                if (messages.isEmpty()) {
                    messages = Optional.of(new LinkedHashMap<>());
                    updateSession((LinkedHashMap<String, String>) messages.get());
                }
            }
        }
        return (LinkedHashMap<String, String>) messages.get();
    }

    /**
     * Updates session with the internalMessages key. Some application servers require session value to be updated
     * in order to replicate the session across nodes or persist it correctly.
     */
    private void updateSession(final LinkedHashMap<String, String> messages) {
        sessionStore.set(context, SAML_STORAGE_KEY, messages);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(final String key) {
        set(key, null);
    }
}
