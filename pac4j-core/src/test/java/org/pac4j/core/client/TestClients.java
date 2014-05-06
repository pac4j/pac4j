/*
  Copyright 2012 - 2014 Jerome Leleu

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
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * This class tests the {@link Clients} class.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class TestClients extends TestCase implements TestsConstants {

    private MockBaseClient newFacebookClient() {
        return new MockBaseClient("FacebookClient");
    }

    private MockBaseClient newYahooClient() {
        return new MockBaseClient("YahooClient");
    }

    public void testMissingClient() {
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clientsGroup, "clients cannot be null");
    }

    public void testMissingCallbackUrl() {
        final Clients clientsGroup = new Clients();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(newFacebookClient());
        TestsHelper.initShouldFail(clientsGroup, "callbackUrl cannot be blank");
    }

    public void testTwoClients() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClientNameParameter(TYPE);
        clientsGroup.setClientsList(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        assertNull(facebookClient.getCallbackUrl());
        assertNull(yahooClient.getCallbackUrl());
        clientsGroup.init();
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        assertEquals(yahooClient,
                clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getName())));
        assertEquals(yahooClient, clientsGroup.findClient(yahooClient.getName()));
    }

    public void testDoubleInit() {
        final MockBaseClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.init();
        final Clients clientsGroup2 = new Clients();
        clientsGroup2.setCallbackUrl(CALLBACK_URL);
        clientsGroup2.setClients(facebookClient);
        clientsGroup2.init();
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
    }

    public void testAllClients() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClientsList(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(2, clients2.size());
        assertTrue(clients2.containsAll(clients));
    }

    public void testClientWithCallbackUrl() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        final MockBaseClient yahooClient = newYahooClient();
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.init();
        assertEquals(LOGIN_URL + "?" + group.getClientNameParameter() + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + yahooClient.getName(),
                yahooClient.getCallbackUrl());
    }

    public void testByClass1() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, facebookClient, fakeClient);
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }

    public void testByClass2() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, fakeClient, facebookClient);
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }
}
