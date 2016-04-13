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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.saml.dbclient.dao.api.DbLoadedSamlClientConfigurationDto;
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

		List<String> names = new ArrayList<String>();
		names.add("Client1");
		names.add("Client2");
		names.add("Client3");
		List<DbLoadedSamlClientConfigurationDto> all = new ArrayList<DbLoadedSamlClientConfigurationDto>();
		DbLoadedSamlClientConfigurationDto client1 = createTestConfig("Client1");
		DbLoadedSamlClientConfigurationDto client2 = createTestConfig("Client2");
		DbLoadedSamlClientConfigurationDto client3 = createTestConfig("Client3");
		all.add(client1);
		all.add(client2);
		all.add(client3);

		when(realDaoMock.loadClientNames()).thenReturn(names);
		when(realDaoMock.loadAllClients()).thenReturn(all);
		when(realDaoMock.loadClient("Client1")).thenReturn(client1);
		when(realDaoMock.loadClient("Client2")).thenReturn(client2);
		when(realDaoMock.loadClient("Client3")).thenReturn(client3);
		
		return realDaoMock;
	}
	
	
	private DbLoadedSamlClientConfigurationDto createTestConfig(String clientName) {
		DbLoadedSamlClientConfigurationDto cfg = new DbLoadedSamlClientConfigurationDto();
		
		cfg.setClientName(clientName);
		cfg.setEnvironment("Env");
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


	
	@Test
	public void testLoadAllNames() {
		// Read 10 times.
		for (int i = 0; i < 10; i++) {
			List<String> names = daoUnderTest.loadClientNames();
			assertNotNull(names);
			assertEquals(3, names.size());
			assertTrue(names.contains("Client1"));
			assertTrue(names.contains("Client2"));
			assertTrue(names.contains("Client3"));
		}		

		// The real DAO must have been called just once.
		verify(realDaoMock, times(0)).loadClientNames();
		verify(realDaoMock, times(1)).loadAllClients();
		verify(realDaoMock, times(0)).loadClient(anyString());
	}
	
	
	/**
	 * Checks that loadAllClients() returns data repeatedly and the underlying DAO is called just once.
	 */
	@Test
	public void testLoadAllClients() {
		// Read 10 times.
		for (int i = 0; i < 10; i++) {
			List<DbLoadedSamlClientConfigurationDto> all = daoUnderTest.loadAllClients();
			assertNotNull(all);
			assertEquals(3, all.size());
		}
		
		// The real DAO must have been called just once.
		verify(realDaoMock, times(0)).loadClientNames();
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
				DbLoadedSamlClientConfigurationDto cfg = daoUnderTest.loadClient(clientName);
				assertNotNull(cfg);
				assertEquals(clientName, cfg.getClientName());
			}
			
			assertNull(daoUnderTest.loadClient("DoesNotExist"));
		}
		
		// The real DAO must have been called just once.
		verify(realDaoMock, times(0)).loadClientNames();
		verify(realDaoMock, times(1)).loadAllClients();
		verify(realDaoMock, times(0)).loadClient(anyString());
	}

}
