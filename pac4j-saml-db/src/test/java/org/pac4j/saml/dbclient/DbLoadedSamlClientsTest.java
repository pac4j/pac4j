package org.pac4j.saml.dbclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.FakeClient;
import org.pac4j.core.client.TestClients;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;



/**
 * Unit test of {@link DbLoadedSamlClientsTest}. Most code is taken from {@link TestClients} but adapted - some checks make no sense here
 * and some operations are not doable.
 * 
 * @author jkacer
 */
public class DbLoadedSamlClientsTest implements TestsConstants {

	private DbLoadedSamlClients clientsUnderTest;
	
	
	@Before
	public void initTestedClients() {
		SamlClientDao samlClientDaoMock = createSamlClientDaoMock();
		clientsUnderTest = new DbLoadedSamlClients(CALLBACK_URL);
		clientsUnderTest.setSamlClientDao(samlClientDaoMock);
	}

	
	private SamlClientDao createSamlClientDaoMock() {
		DbLoadedSamlClientConfiguration clientCfg1 = new DbLoadedSamlClientConfiguration();
		clientCfg1.setClientName("SamlClient1"); // The rest is not important for the test
		DbLoadedSamlClientConfiguration clientCfg2 = new DbLoadedSamlClientConfiguration();
		clientCfg2.setClientName("SamlClient2"); // The rest is not important for the test
		
		List<DbLoadedSamlClientConfiguration> clientConfigurations = new ArrayList<DbLoadedSamlClientConfiguration>();
		clientConfigurations.add(clientCfg1);
		clientConfigurations.add(clientCfg2);

		SamlClientDao scd = mock(SamlClientDao.class);
		when(scd.loadAllClients()).thenReturn(clientConfigurations);
		
		return scd;
	}


	@Test
    public void testMissingCallbackUrl() {
		clientsUnderTest.setCallbackUrl(null);
        TestsHelper.initShouldFail(clientsUnderTest, "callbackUrl cannot be blank");
    }
	
	
	@Test
    public void testTwoClients() {
		clientsUnderTest.init(null);
        DbLoadedSamlClient c0 = clientsUnderTest.findClient(MockWebContext.create().addRequestParameter("client_name", "SamlClient1"));
        assertNotNull(c0);
        assertEquals("SamlClient1", c0.getName());

        clientsUnderTest.setClientNameParameter(TYPE);
        clientsUnderTest.reinit(null);
        
        DbLoadedSamlClient c1 = clientsUnderTest.findClient(MockWebContext.create().addRequestParameter(TYPE, "SamlClient1"));
        assertNotNull(c1);
        assertEquals("SamlClient1", c1.getName());
        
        DbLoadedSamlClient c2 = clientsUnderTest.findClient(null, "SamlClient2");
        assertNotNull(c2);
        assertEquals("SamlClient2", c2.getName());
    }

	
	@Test
    public void testDoubleInit() {
        clientsUnderTest.init(null);
        clientsUnderTest.init(null);
        DbLoadedSamlClient foundClient = clientsUnderTest.findClient(null, "SamlClient1");
        assertNotNull(foundClient);
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + "SamlClient1", foundClient.getCallbackUrl());
    }


	@Test
    public void testAllClients() {
        final List<DbLoadedSamlClient> clients = clientsUnderTest.findAllClients(null);
        assertEquals(2, clients.size());
        assertTrue(listContainsClientWithName(clients, "SamlClient1"));
        assertTrue(listContainsClientWithName(clients, "SamlClient2"));
    }

    
	@Test
    public void testByClass1() {
		DbLoadedSamlClient c = clientsUnderTest.findClient(null, DbLoadedSamlClient.class);
		assertNotNull(c);
		assertTrue(c.getName().startsWith("SamlClient"));
    }
	
	
	@Test(expected=TechnicalException.class)
    public void testByClass2() {
		DbLoadedSamlClient c = clientsUnderTest.findClient(null, FakeClient.class);
		assertNull(c);
    }

	
	private boolean listContainsClientWithName(List<DbLoadedSamlClient> clients, String name) {
		for (DbLoadedSamlClient c: clients) {
			if (c.getName().equals(name)) {return true;}
		}
		return false;
	}

}
