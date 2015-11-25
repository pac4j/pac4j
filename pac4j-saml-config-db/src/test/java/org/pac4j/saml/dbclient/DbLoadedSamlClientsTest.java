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
package org.pac4j.saml.dbclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.FakeClient;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.MockBaseClient;
import org.pac4j.core.client.TestClients;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.dao.api.DbLoadedSamlClientConfigurationDto;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;



/**
 * Unit test of {@link DbLoadedSamlClients}. Most code is taken from {@link TestClients} and slightly adapted.
 * A few test methods added, as well as a few checks to existing test methods.
 * 
 * @author jkacer
 */
public class DbLoadedSamlClientsTest implements TestsConstants {

    private MockBaseClient newFacebookClient() {
        return new MockBaseClient("FacebookClient");
    }

    private MockBaseClient newYahooClient() {
        return new MockBaseClient("YahooClient");
    }

    @Test
    public void testMissingClient() {
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        clientsGroup.init(); // This should not fail anymore because the DAO contributes some clients too
    }

    @Test
    public void testNoCallbackUrl() {
        MockBaseClient facebookClient = newFacebookClient();
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients(facebookClient);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        clientsGroup.init();
        assertNull(facebookClient.getCallbackUrl());
        IndirectClient sc1 = (IndirectClient) clientsGroup.findClient("SamlClient1");
        assertNotNull(sc1);
        assertNull(sc1.getCallbackUrl());
    }

    @Test
    public void testTwoClients() { // In fact, they are four at the end :-)
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients();
        clientsGroup.setClientNameParameter(TYPE);
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        assertNull(facebookClient.getCallbackUrl());
        assertNull(yahooClient.getCallbackUrl());
        clientsGroup.init();
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        assertEquals(yahooClient, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getName())));
        assertEquals(yahooClient, clientsGroup.findClient(yahooClient.getName()));
        // Additional 2 clients from the DAO
        assertNotNull(clientsGroup.findClient("SamlClient1"));
        assertNotNull(clientsGroup.findClient("SamlClient2"));
        assertNotNull(clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, "SamlClient1")));
        assertNotNull(clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, "SamlClient2")));
    }

    @Test
    public void testDoubleInit() {
        final MockBaseClient facebookClient = newFacebookClient();
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        clientsGroup.init();
        final DbLoadedSamlClients clientsGroup2 = new DbLoadedSamlClients();
        clientsGroup2.setCallbackUrl(CALLBACK_URL);
        clientsGroup2.setClients(facebookClient);
        clientsGroup2.setSamlClientDao(createSamlClientDaoMock());
        clientsGroup2.init();
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
    }
    
    
    @Test
    public void testDoubleInitOnSameGroup() {
        final MockBaseClient facebookClient = newFacebookClient();
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        clientsGroup.init();
        clientsGroup.init();
    	
        final List<Client> clients = clientsGroup.findAllClients();
        assertEquals(3, clients.size()); // 1 explicit + 2 contributed by the DAO
        assertTrue(clients.contains(facebookClient));
    }
    
    
    @Test
    public void testReInit() {
        final MockBaseClient facebookClient = newFacebookClient();
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        clientsGroup.init();
        clientsGroup.reinit();
    	
        final List<Client> clients = clientsGroup.findAllClients();
        assertEquals(3, clients.size()); // 1 explicit + 2 contributed by the DAO
        assertTrue(clients.contains(facebookClient));
    }
        

    @Test
    public void testAllClients() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<Client>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final DbLoadedSamlClients clientsGroup = new DbLoadedSamlClients();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setSamlClientDao(createSamlClientDaoMock());
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(4, clients2.size()); // 2 explicit + 2 contributed by the DAO
        assertTrue(clients2.containsAll(clients));
    }

    @Test
    public void testClientWithCallbackUrl() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        final MockBaseClient yahooClient = newYahooClient();
        final DbLoadedSamlClients group = new DbLoadedSamlClients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.setSamlClientDao(createSamlClientDaoMock());
        group.init();
        assertEquals(LOGIN_URL + "?" + group.getClientNameParameter() + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        SAML2Client samlClient = (SAML2Client) group.findClient("SamlClient1");
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + samlClient.getName(), samlClient.getCallbackUrl());
    }

    @Test
    public void testClientWithCallbackUrlWithoutIncludingClientName() {
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        facebookClient.setIncludeClientNameInCallbackUrl(false);
        final MockBaseClient yahooClient = newYahooClient();
        yahooClient.setIncludeClientNameInCallbackUrl(false);
        final DbLoadedSamlClients group = new DbLoadedSamlClients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.setSamlClientDao(createSamlClientDaoMock());
        group.init();
        assertEquals(LOGIN_URL, facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL, yahooClient.getCallbackUrl());
        SAML2Client samlClient = (SAML2Client) group.findClient("SamlClient1");
        assertEquals(CALLBACK_URL + "?" + KEY + "=" + samlClient.getName(), samlClient.getCallbackUrl());
    }

    @Test
    public void testByClass1() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final DbLoadedSamlClients clients = new DbLoadedSamlClients(CALLBACK_URL, facebookClient, fakeClient);
        clients.setSamlClientDao(createSamlClientDaoMock());
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
        SAML2Client samlClient = clients.findClient(SAML2Client.class);
        assertNotNull(samlClient);
        assertTrue(samlClient.getName().startsWith("SamlClient")); // SamlClient1 or SamlClient2
    }

    @Test
    public void testByClass2() {
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final DbLoadedSamlClients clients = new DbLoadedSamlClients(CALLBACK_URL, fakeClient, facebookClient);
        clients.setSamlClientDao(createSamlClientDaoMock());
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
        SAML2Client samlClient = clients.findClient(SAML2Client.class);
        assertNotNull(samlClient);
        assertTrue(samlClient.getName().startsWith("SamlClient")); // SamlClient1 or SamlClient2
    }

    @Test(expected = TechnicalException.class)
    public void rejectSameName() {
        final MockBaseClient client1 = new MockBaseClient(NAME);
        final MockBaseClient client2 = new MockBaseClient(NAME);
        final DbLoadedSamlClients clients = new DbLoadedSamlClients(CALLBACK_URL, client1, client2);
        clients.setSamlClientDao(createSamlClientDaoMock());
        clients.init();
    }

    
	private SamlClientDao createSamlClientDaoMock() {
		DbLoadedSamlClientConfigurationDto clientCfg1 = new DbLoadedSamlClientConfigurationDto();
		clientCfg1.setClientName("SamlClient1"); // The rest is not important for the test
		DbLoadedSamlClientConfigurationDto clientCfg2 = new DbLoadedSamlClientConfigurationDto();
		clientCfg2.setClientName("SamlClient2"); // The rest is not important for the test
		
		List<DbLoadedSamlClientConfigurationDto> clientConfigurationDtos = new ArrayList<DbLoadedSamlClientConfigurationDto>();
		clientConfigurationDtos.add(clientCfg1);
		clientConfigurationDtos.add(clientCfg2);
		
		List<String> clientNames = new ArrayList<String>();
		clientNames.add("SamlClient1");
		clientNames.add("SamlClient2");

		SamlClientDao scd = mock(SamlClientDao.class);
		when(scd.loadAllClients()).thenReturn(clientConfigurationDtos);
		when(scd.loadClientNames()).thenReturn(clientNames);
		when(scd.loadClient("SamlClient1")).thenReturn(clientCfg1);
		when(scd.loadClient("SamlClient2")).thenReturn(clientCfg2);
		
		return scd;
	}

}
