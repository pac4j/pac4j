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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

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
    
    private MockBaseClient newContributedClient(int ordinalNumber) {
    	return new MockBaseClient("ContributedClient" + ordinalNumber);
    }

    @Test
    public void testMissingClient() {
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clientsGroup, "clients cannot be null");
    }

    @Test
    public void testNoStaticClientButContributed() {
        final MockBaseClient contributedClient = newContributedClient(1);
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClientContributors(createMockContributorList(contributedClient));
        clientsGroup.setClients(new ArrayList<Client>()); // Empty list of static clients, 1 will be contributed
        clientsGroup.init();
        List<Client> all = clientsGroup.findAllClients();
        assertNotNull(all);
        assertEquals(1, all.size());
    }

    @Test
    public void testNoCallbackUrl() {
        MockBaseClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients(facebookClient);
        clientsGroup.init();
        assertNull(facebookClient.getCallbackUrl());
    }

    @Test
    public void testNoCallbackUrlWithContributed() {
        MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient contributedClient = newContributedClient(1);
        final Clients clientsGroup = new Clients(facebookClient);
        clientsGroup.setClientContributors(createMockContributorList(contributedClient));
        clientsGroup.init();
        assertNull(facebookClient.getCallbackUrl());
        assertNull(contributedClient.getCallbackUrl());
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
        clientsGroup.init();
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        assertEquals(yahooClient,
                clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getName())));
        assertEquals(yahooClient, clientsGroup.findClient(yahooClient.getName()));
    }

    @Test
    public void testTwoClientsAndOneContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final MockBaseClient contributedClient = newContributedClient(1);
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClientNameParameter(TYPE);
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClientContributors(createMockContributorList(contributedClient));
        assertNull(facebookClient.getCallbackUrl());
        assertNull(yahooClient.getCallbackUrl());
        assertNull(contributedClient.getCallbackUrl());
        clientsGroup.init();
        
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + contributedClient.getName(), contributedClient.getCallbackUrl());
        assertEquals(yahooClient,
                clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getName())));
        assertEquals(yahooClient, clientsGroup.findClient(yahooClient.getName()));
        assertEquals(contributedClient,
                clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, contributedClient.getName())));
        assertEquals(contributedClient, clientsGroup.findClient(contributedClient.getName()));
    }

    @Test
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

    @Test
    public void testDoubleInitWithContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient contributedClient = newContributedClient(1);
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.setClientContributors(createMockContributorList(contributedClient));
        clientsGroup.init();
        final Clients clientsGroup2 = new Clients();
        clientsGroup2.setCallbackUrl(CALLBACK_URL);
        clientsGroup2.setClients(facebookClient);
        clientsGroup.setClientContributors(createMockContributorList(contributedClient));
        clientsGroup2.init();
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + contributedClient.getName(),
                contributedClient.getCallbackUrl());
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
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(2, clients2.size());
        assertTrue(clients2.containsAll(clients));
    }

    @Test
    public void testAllClientsWithContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final MockBaseClient contributedClient = newContributedClient(1);
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        
        final Clients clientsGroup = new Clients();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClientContributors(createMockContributorList(contributedClient));
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(3, clients2.size());
        assertTrue(clients2.containsAll(clients));
        assertTrue(clients2.contains(contributedClient));
    }

	@Test
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

	@Test
    public void testClientWithCallbackUrlWithContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        final MockBaseClient yahooClient = newYahooClient();
        final MockBaseClient contributedClient = newContributedClient(1);
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.setClientContributors(createMockContributorList(contributedClient));
        group.init();
        assertEquals(LOGIN_URL + "?" + group.getClientNameParameter() + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + yahooClient.getName(),
                yahooClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + contributedClient.getName(),
                contributedClient.getCallbackUrl());
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
        group.init();
        assertEquals(LOGIN_URL, facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL, yahooClient.getCallbackUrl());
    }

    @Test
    public void testClientWithCallbackUrlWithoutIncludingClientNameWithContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        facebookClient.setIncludeClientNameInCallbackUrl(false);
        final MockBaseClient yahooClient = newYahooClient();
        yahooClient.setIncludeClientNameInCallbackUrl(false);
        
        final MockBaseClient contributedClient1 = newContributedClient(1);
        contributedClient1.setIncludeClientNameInCallbackUrl(false);
        final MockBaseClient contributedClient2 = newContributedClient(2);
        contributedClient2.setCallbackUrl(LOGIN_URL);
        contributedClient2.setIncludeClientNameInCallbackUrl(false);
        
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.setClientContributors(createMockContributorList(contributedClient1, contributedClient2));
        group.init();
        assertEquals(LOGIN_URL, facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL, yahooClient.getCallbackUrl());
        assertEquals(CALLBACK_URL, contributedClient1.getCallbackUrl());
        assertEquals(LOGIN_URL, contributedClient2.getCallbackUrl());
    }

    @Test
    public void testByClass1() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, facebookClient, fakeClient);
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }

    @Test
    public void testByClass1WithContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, facebookClient);
        clients.setClientContributors(createMockContributorList(fakeClient));
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }

    @Test
    public void testByClass2() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, fakeClient, facebookClient);
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }

    @Test
    public void testByClass2WithContributed() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients = new Clients(CALLBACK_URL, fakeClient);
        clients.setClientContributors(createMockContributorList(facebookClient));
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }

    @Test(expected = TechnicalException.class)
    public void rejectSameName() {
        final MockBaseClient client1 = new MockBaseClient(NAME);
        final MockBaseClient client2 = new MockBaseClient(NAME);
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        clients.init();
    }

    @Test(expected = TechnicalException.class)
    public void rejectSameNameWithContributed() {
        final MockBaseClient client1 = new MockBaseClient(NAME);
        final MockBaseClient client2 = new MockBaseClient(NAME);
        final Clients clients = new Clients(CALLBACK_URL, client1);
        clients.setClientContributors(createMockContributorList(client2));
        clients.init();
    }

    
    /**
     * Creates a client contributor list with just one contributor, which contributes all clients passed in.
     * @param clients The clients to be contributed.
     * @return A list containing 1 contributor.
     */
    private List<AdditionalClientContributor> createMockContributorList(Client... clients) {
    	AdditionalClientContributor contributor1 = mock(AdditionalClientContributor.class);
    	when(contributor1.contributeClients()).thenReturn(new HashSet<>(Arrays.asList(clients)));
    	
    	List<AdditionalClientContributor> contributors = new ArrayList<>();
    	contributors.add(contributor1);
		return contributors;
	}

}
