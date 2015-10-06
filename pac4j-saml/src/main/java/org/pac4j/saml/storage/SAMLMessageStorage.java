/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
