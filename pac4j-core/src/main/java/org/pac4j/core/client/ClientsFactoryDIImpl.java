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
package org.pac4j.core.client;

import java.util.List;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;

/**
 * A Spring-initializable factory to build clients.
 * 
 * @author Daniel Hankins
 * @since 1.8.0
 *
 */
public class ClientsFactoryDIImpl implements ClientsFactory {

    /**
     * The list of clients to be initialized by dependency injection and
     * returned by build()
     */
    private Clients clientList;

    /**
     * Constructor that Spring can use to inject the client list to be returned
     * by build()
     * 
     * @param clientList
     */
    public ClientsFactoryDIImpl(List<Client> clientList) {
        this.clientList = new Clients(clientList);
    }

    /**
     * Return the list of clients injected by Spring. This implementation
     * ignores the env object, as the list has already been built by Spring.
     * 
     * @see org.pac4j.core.client.ClientsFactory#build(java.lang.Object)
     */
    @Override
    public Clients build(Object env) {
        return this.clientList;
    }

}
