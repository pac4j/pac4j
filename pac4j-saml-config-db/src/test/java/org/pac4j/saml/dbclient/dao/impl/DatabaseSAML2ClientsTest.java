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
package org.pac4j.saml.dbclient.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.DatabaseSAML2Clients;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;


/**
 * Unit test of {@link DatabaseSAML2Clients}. Most tests taken from {@code TestClients} but slightly adapted to make sense with
 * {@code DatabaseSAML2Clients}.
 * 
 * @author jkacer
 * @since 1.9.0
 */
@SuppressWarnings("rawtypes")
public class DatabaseSAML2ClientsTest implements TestsConstants {

    @Test
    public void testMissingClient() {
    	// DatabaseSAML2Clients does not fail anymore in init() if there are no clients defined.
        final DatabaseSAML2Clients clientsGroup = new DatabaseSAML2Clients(createSamlClientDaoMock());
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.init();
        List<Client> clients = clientsGroup.getClients();
        assertNotNull(clients);
        assertEquals(3, clients.size());
    }

    @Test
    public void testNoCallbackUrl() {
        final DatabaseSAML2Clients clientsGroup = new DatabaseSAML2Clients(createSamlClientDaoMock());//(facebookClient);
        clientsGroup.init();
        Client c = clientsGroup.findClient("SamlOne");
        assertNotNull(c);
        assertNull(((SAML2Client)c).getCallbackUrl());
    }

    @Test
    public void testThreeClients() {
        final DatabaseSAML2Clients clientsGroup = new DatabaseSAML2Clients(createSamlClientDaoMock());
        clientsGroup.setClientNameParameter(TYPE);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        
        Client c1 = clientsGroup.findClient("SamlOne"); // findClient() involves init()
        Client c2 = clientsGroup.findClient("SamlTwo");
        Client c3 = clientsGroup.findClient("SamlThree");
        assertNotNull(c1);
        assertNotNull(c2);
        assertNotNull(c3);
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + c1.getName(), ((SAML2Client)c1).getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + c2.getName(), ((SAML2Client)c2).getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + c3.getName(), ((SAML2Client)c3).getCallbackUrl());
        assertEquals(c1, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, c1.getName())));
        assertEquals(c2, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, c2.getName())));
        assertEquals(c3, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, c3.getName())));

        // Explicit init() should not change anything
        clientsGroup.init();
        c1 = clientsGroup.findClient("SamlOne");
        c2 = clientsGroup.findClient("SamlTwo");
        c3 = clientsGroup.findClient("SamlThree");
        assertNotNull(c1);
        assertNotNull(c2);
        assertNotNull(c3);
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + c1.getName(), ((SAML2Client)c1).getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + c2.getName(), ((SAML2Client)c2).getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + c3.getName(), ((SAML2Client)c3).getCallbackUrl());
        assertEquals(c1, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, c1.getName())));
        assertEquals(c2, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, c2.getName())));
        assertEquals(c3, clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, c3.getName())));
    }

    @Test
    public void testDoubleInit() {
        final DatabaseSAML2Clients clientsGroup = new DatabaseSAML2Clients(createSamlClientDaoMock());
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.init();
        final DatabaseSAML2Clients clientsGroup2 = new DatabaseSAML2Clients(createSamlClientDaoMock());
        clientsGroup2.setCallbackUrl(CALLBACK_URL);
        clientsGroup2.init();
        Client c1 = clientsGroup.findClient("SamlOne");
        assertEquals(CALLBACK_URL + "?" + DatabaseSAML2Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + c1.getName(), ((SAML2Client)c1).getCallbackUrl());
    }

    @Test
    public void testAllClients() {
        final DatabaseSAML2Clients clientsGroup = new DatabaseSAML2Clients(createSamlClientDaoMock());
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(3, clients2.size());
        String[] expectedNames = new String[] {"SamlOne", "SamlTwo", "SamlThree"};
        for (String expName: expectedNames) {
        	assertTrue(clientsContain(clients2, expName));
        }
    }

    private boolean clientsContain(List<Client> clients, String expName) {
		for (Client c : clients) {
			if (c.getName().equals(expName))
				return true;
		}
		return false;
	}

	@Test
    public void testClientWithCallbackUrl() {
        final DatabaseSAML2Clients group = new DatabaseSAML2Clients(createSamlClientDaoMock());
        group.setClientNameParameter(KEY);
        group.setCallbackUrl(CALLBACK_URL);
        group.init();
        Client c1 = group.findClient("SamlOne");
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + c1.getName(), ((SAML2Client) c1).getCallbackUrl());
    }

    @Test
    public void testByClassFindsSaml() {
        final DatabaseSAML2Clients clients = new DatabaseSAML2Clients(createSamlClientDaoMock());
        assertNotNull(clients.findClient(SAML2Client.class));
    }
    
    @Test(expected = TechnicalException.class)
    public void rejectSameName() {
        final DatabaseSAML2Clients clients = new DatabaseSAML2Clients(createSamlClientDaoMockWithDuplicateNames());
        clients.init();
    }

   
	private SamlClientDao createSamlClientDaoMock() {
		List<String> fakeNames = Arrays.asList("SamlOne", "SamlTwo", "SamlThree");
		SamlClientDao dao = mock(SamlClientDao.class);
		when(dao.loadClientNames()).thenReturn(fakeNames);
		return dao;
	}

	private SamlClientDao createSamlClientDaoMockWithDuplicateNames() {
		List<String> fakeNames = Arrays.asList("SamlOne", "SamlOne");
		SamlClientDao dao = mock(SamlClientDao.class);
		when(dao.loadClientNames()).thenReturn(fakeNames);
		return dao;
	}

}
