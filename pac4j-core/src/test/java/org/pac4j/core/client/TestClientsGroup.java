/*
  Copyright 2012 - 2013 Jerome Leleu

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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * This class tests the {@link ClientsGroup} class.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class TestClientsGroup extends TestCase implements TestsConstants {
    
    private MockBaseClient newFacebookClient() {
        return new MockBaseClient("FacebookClient");
    }
    
    private MockBaseClient newYahooClient() {
        return new MockBaseClient("YahooClient");
    }
    
    public void testMissingClient() {
        final ClientsGroup clientsGroup = new ClientsGroup();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clientsGroup, "clients cannot be null");
    }
    
    public void testMissingCallbackUrl() {
        final ClientsGroup clientsGroup = new ClientsGroup();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(newFacebookClient());
        TestsHelper.initShouldFail(clientsGroup, "callbackUrl cannot be blank");
    }
    
    public void testOneClient() throws ClientException {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(CALLBACK_URL);
        final ClientsGroup clientsGroup = new ClientsGroup();
        clientsGroup.buildFromOneClient(facebookClient);
        clientsGroup.init();
        clientsGroup.init();
        assertEquals(CALLBACK_URL + "?" + ClientsGroup.DEFAULT_CLIENT_TYPE_PARAMETER + "=" + facebookClient.getType(),
                     facebookClient.getCallbackUrl());
        assertEquals(facebookClient, clientsGroup.findClient(MockWebContext.create()
            .addRequestParameter(ClientsGroup.DEFAULT_CLIENT_TYPE_PARAMETER, facebookClient.getType())));
        assertEquals(facebookClient, clientsGroup.findClient(facebookClient.getType()));
    }
    
    public void testTwoClients() throws ClientException {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final ClientsGroup clientsGroup = new ClientsGroup();
        clientsGroup.setClientTypeParameter(TYPE);
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        assertNull(facebookClient.getCallbackUrl());
        assertNull(yahooClient.getCallbackUrl());
        clientsGroup.init();
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getType(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getType(), yahooClient.getCallbackUrl());
        assertEquals(yahooClient,
                     clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getType())));
        assertEquals(yahooClient, clientsGroup.findClient(yahooClient.getType()));
    }
    
    public void testDoubleInit() throws ClientException {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(CALLBACK_URL);
        final ClientsGroup clientsGroup = new ClientsGroup();
        clientsGroup.buildFromOneClient(facebookClient);
        clientsGroup.init();
        final ClientsGroup clientsGroup2 = new ClientsGroup();
        clientsGroup2.buildFromOneClient(facebookClient);
        clientsGroup2.init();
        assertEquals(CALLBACK_URL + "?" + ClientsGroup.DEFAULT_CLIENT_TYPE_PARAMETER + "=" + facebookClient.getType(),
                     facebookClient.getCallbackUrl());
    }
    
    public void testAllClients() throws ClientException {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final ClientsGroup clientsGroup = new ClientsGroup();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(2, clients2.size());
        assertTrue(clients2.containsAll(clients));
    }
    
    public void testFailureUrlDefinedInGroup() throws ClientException {
        final MockBaseClient facebookClient = newFacebookClient();
        final ClientsGroup clientsGroup = new ClientsGroup(CALLBACK_URL, FAILURE_URL, facebookClient);
        clientsGroup.init();
        assertEquals(FAILURE_URL, facebookClient.getFailureUrl());
    }
    
    public void testFailureUrlDefinedInBoth() throws ClientException {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setFailureUrl(FAILURE_URL2);
        final ClientsGroup clientsGroup = new ClientsGroup(CALLBACK_URL, FAILURE_URL, facebookClient);
        clientsGroup.init();
        assertEquals(FAILURE_URL2, facebookClient.getFailureUrl());
    }
}
