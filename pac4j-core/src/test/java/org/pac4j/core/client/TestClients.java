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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * This class tests the {@link Clients} class.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class TestClients implements TestsConstants {

    private MockBaseClient newFacebookClient() {
        return new MockBaseClient("FacebookClient");
    }

    private MockBaseClient newYahooClient() {
        return new MockBaseClient("YahooClient");
    }

    @Test
    public void testMissingClient() {
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clientsGroup, "clients cannot be null");
    }

    @Test
    public void testNoCallbackUrl() {
        MockBaseClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients(facebookClient);
        clientsGroup.init(null);
        assertNull(facebookClient.getCallbackUrl());
    }

    @Test
    public void testTwoClients() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClientNameParameter(TYPE);
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        assertNull(facebookClient.getCallbackUrl());
        assertNull(yahooClient.getCallbackUrl());
        clientsGroup.init(null);
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        assertEquals(yahooClient,
                clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getName())));
        assertEquals(yahooClient, clientsGroup.findClient(null, yahooClient.getName()));
    }

    @Test
    public void testDoubleInit() {
        final MockBaseClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.init(null);
        final Clients clientsGroup2 = new Clients();
        clientsGroup2.setCallbackUrl(CALLBACK_URL);
        clientsGroup2.setClients(facebookClient);
        clientsGroup2.init(null);
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
    }

    @Test
    public void testAllClients() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        final List<Client> clients2 = clientsGroup.findAllClients(null);
        assertEquals(2, clients2.size());
        assertTrue(clients2.containsAll(clients));
    }

    @Test
    public void testClientWithCallbackUrl() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        final MockBaseClient yahooClient = newYahooClient();
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.init(null);
        assertEquals(LOGIN_URL + "?" + group.getClientNameParameter() + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + yahooClient.getName(),
                yahooClient.getCallbackUrl());
    }

    @Test
    public void testClientWithCallbackUrlWithoutIncludingClientName() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        facebookClient.setIncludeClientNameInCallbackUrl(false);
        final MockBaseClient yahooClient = newYahooClient();
        yahooClient.setIncludeClientNameInCallbackUrl(false);
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.init(null);
        assertEquals(LOGIN_URL, facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL, yahooClient.getCallbackUrl());
    }

    @Test
    public void testByClass1() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, facebookClient, fakeClient);
        assertEquals(facebookClient, clients.findClient(null, MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(null, FakeClient.class));
    }

    @Test
    public void testByClass2() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, fakeClient, facebookClient);
        assertEquals(facebookClient, clients.findClient(null, MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(null, FakeClient.class));
    }

    @Test(expected = TechnicalException.class)
    public void rejectSameName() {
        final MockBaseClient client1 = new MockBaseClient(NAME);
        final MockBaseClient client2 = new MockBaseClient(NAME);
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        clients.init(null);
    }
}
