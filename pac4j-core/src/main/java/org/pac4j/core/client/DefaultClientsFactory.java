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
 * A concrete factory to build clients meant to be initialized by dependency
 * injection.
 * 
 * @author Daniel Hankins
 * @since 1.8.0
 *
 */
public class DefaultClientsFactory implements ClientsFactory {

    /**
     * The list of clients meant to be initialized by dependency injection and
     * returned by build()
     */
    private Clients clientList;

    /**
     * Constructor meant for dependency injection to inject the
     * client list to be returned by build()
     * 
     * @param clientList The list of clients to be returned by build().
     */
    public DefaultClientsFactory(final List<Client> clientList) {
        this.clientList = new Clients(clientList);
    }

    /**
     * Constructor meant for dependency injection to inject the
     * client list to be returned by build()
     * 
     * @param client The list of clients to be returned by build().
     */
    public DefaultClientsFactory(final Client... client) {
        this.clientList = new Clients(client);
    }

    /**
     * Constructor meant for dependency injection to inject the
     * client list to be returned by build()
     * 
     * @param callbackUrl The URL to be forwarded to for authentication.
     * @param clientList The list of clients to be returned by build().
    */
    public DefaultClientsFactory(final String callbackUrl, final List<Client> clientList) {
        this.clientList = new Clients(callbackUrl, clientList);
    }

    /**
     * Constructor meant for dependency injection to inject the
     * client list to be returned by build()
     * 
     * @param callbackUrl The URL to be forwarded to for authentication.
     * @param client The list of clients to be returned by build().
     */
    public DefaultClientsFactory(final String callbackUrl, final Client... client) {
        this.clientList = new Clients(callbackUrl, client);
    }

    /**
     * Return the list of clients injected in the constructor. This
     * implementation ignores the env object, as the list has already been built
     * by the constructor's caller.
     */
    @Override
    public Clients build(final Object env) {
        return this.clientList;
    }

}
