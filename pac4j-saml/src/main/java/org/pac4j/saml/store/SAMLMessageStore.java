package org.pac4j.saml.store;

import org.opensaml.core.xml.XMLObject;
import org.pac4j.core.store.Store;

/**
 * Implementations serve as data stores for sent/received SAML messages. Potential implementations could
 * be using for example central repository common for all users within the application or HttpSession.
 * <p>
 * Messages may need to be stored for example to pair a response with an original request.
 *
 * @author Vladimir Schäfer
 */
public interface SAMLMessageStore extends Store<String, XMLObject> {
}
