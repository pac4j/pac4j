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

import static org.junit.Assert.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.saml.dbclient.DbLoadedSamlClientConfiguration;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;


/**
 * Unit test of {@link CachingSamlClientDaoImpl}.
 * 
 * @author jkacer
 */
public class CachingSamlClientDaoImplTest {

	/** The tested DAO. */
	private CachingSamlClientDaoImpl daoUnderTest;
	
	/** An underlying DAO mock. */
	private SamlClientDao realDaoMock;
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	
	@Before
	public void initializeTestedDao() {
		realDaoMock = createRealDaoMock();
		daoUnderTest = new CachingSamlClientDaoImpl(realDaoMock);
	}
	
	
	private SamlClientDao createRealDaoMock() {
		SamlClientDao realDaoMock = mock(SamlClientDao.class);

		List<DbLoadedSamlClientConfiguration> all = new ArrayList<DbLoadedSamlClientConfiguration>();
		DbLoadedSamlClientConfiguration client1 = createTestConfig("Client1");
		DbLoadedSamlClientConfiguration client2 = createTestConfig("Client2");
		DbLoadedSamlClientConfiguration client3 = createTestConfig("Client3");
		all.add(client1);
		all.add(client2);
		all.add(client3);
		
		when(realDaoMock.loadAllClients()).thenReturn(all);
		when(realDaoMock.loadClient("Client1")).thenReturn(client1);
		when(realDaoMock.loadClient("Client2")).thenReturn(client2);
		when(realDaoMock.loadClient("Client3")).thenReturn(client3);
		
		return realDaoMock;
	}
	
	
	private DbLoadedSamlClientConfiguration createTestConfig(String clientName) {
		DbLoadedSamlClientConfiguration cfg = new DbLoadedSamlClientConfiguration();
		
		cfg.setClientName(clientName);
		cfg.setIdentityProviderEntityId("urn:idp" + clientName);
		cfg.setServiceProviderEntityId("urn:sp" + clientName);
		cfg.setIdentityProviderMetadata("Some XML here...");
		cfg.setKeystoreBinaryData(createExpectedKeystoreData((byte) 10));
		cfg.setKeystorePassword("Bla");
		cfg.setPrivateKeyPassword("Ble");
		cfg.setMaximumAuthenticationLifetime(111);
		
		return cfg;
	}

	
	private byte[] createExpectedKeystoreData(byte b) {
		byte[] result = new byte[5];
		Arrays.fill(result, b);
		return result;
	}


	/**
	 * Checks that loadAllClients() returns data repeatedly and the underlying DAO is called just once.
	 */
	@Test
	public void testLoadAllClients() {
		// Read 10 times.
		for (int i = 0; i < 10; i++) {
			List<DbLoadedSamlClientConfiguration> all = daoUnderTest.loadAllClients();
			assertNotNull(all);
			assertEquals(3, all.size());
		}
		
		// The real DAO must have been called just once.
		verify(realDaoMock, times(1)).loadAllClients();
		verify(realDaoMock, times(0)).loadClient(anyString());
	}

	
	/**
	 * Checks that loadClient() returns data for fifferent clients repeatedly and the underlying DAO is called just once.
	 */
	@Test
	public void testLoadClient() {
		String[] clientNames = {"Client1", "Client2", "Client3"};
		
		// Read 10 times.
		for (int i = 0; i < 10; i++) {
			for (String clientName: clientNames) {
				DbLoadedSamlClientConfiguration cfg = daoUnderTest.loadClient(clientName);
				assertNotNull(cfg);
				assertEquals(clientName, cfg.getClientName());
			}
			
			assertNull(daoUnderTest.loadClient("DoesNotExist"));
		}
		
		// The real DAO must have been called just once.
		verify(realDaoMock, times(1)).loadAllClients();
		verify(realDaoMock, times(0)).loadClient(anyString());
	}

}
