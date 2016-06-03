package org.pac4j.saml.storage;


import org.opensaml.core.xml.XMLObject;

/**
 * Implementations serve as data stores for sent/received SAML messages. Potential implementations could
 * be using for example central repository common for all users within the application or HttpSession.
 * <p>
 * Messages may need to be stored for example to pair a response with an original request.
 *
 * @author Vladimir Schäfer
 */
public interface SAMLMessageStorage {

    /**
     * Stores given message in the data store. Request must have the ID filled.
     *
     * @param messageId key under which will the message be stored
     * @param message   message to store
     */
    void storeMessage(String messageId, XMLObject message);

    /**
     * Retrieves message stored under given ID.
     *
     * @param messageID message ID to look up
     * @return request or null if not found
     */
    XMLObject retrieveMessage(String messageID);

}
