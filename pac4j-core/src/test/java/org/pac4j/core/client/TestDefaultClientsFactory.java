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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * This class tests the {@link DefaultClientsFactory} class.
 * 
 * @author Daniel Hankins
 * @since 1.8.0
 *
 */
public class TestDefaultClientsFactory {
    /*
     * Test method for DefaultClientsFactory class
     * {@link org.pac4j.core.client.DefaultClientsFactory}
     * .
     */
    @Test
    public final void testDefaultClientsFactory() {
        Client clientA = new MockBaseClient("client a");
        Client clientB = new MockBaseClient("client b");
        Client clientC = new MockBaseClient("client c");
        ArrayList<Client> clientList = new ArrayList<Client>();
        clientList.add(clientA);
        clientList.add(clientB);
        clientList.add(clientC);
        ClientsFactory clientsFactory = new DefaultClientsFactory(clientList);
        Clients clients = clientsFactory.build(null);
        assertEquals(clientA, clients.findClient("client a"));
        assertEquals(clientB, clients.findClient("client b"));
        assertEquals(clientC, clients.findClient("client c"));
    }
}
