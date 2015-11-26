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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;


/**
 * Unit test of {@link DatabaseSAML2ClientContributor}.
 * 
 * @author jkacer
 */
public class DatabaseSAML2ClientContributorTest {

	private DatabaseSAML2ClientContributor contributorUnderTest;
	
	@Before
	public void initTestedContributor() {
		SamlClientDao samlClientDaoMock = createSamlClientDaoMock();
		contributorUnderTest = new DatabaseSAML2ClientContributor(samlClientDaoMock);
	}

	private SamlClientDao createSamlClientDaoMock() {
		List<String> fakeNames = Arrays.asList("SamlOne", "SamlTwo", "SamlThree");
		SamlClientDao dao = mock(SamlClientDao.class);
		when(dao.loadClientNames()).thenReturn(fakeNames);
		return dao;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Tests that the contributor turns database records into SAML2 Clients and their configurations are Database SAML2 Client
	 * Configurations. This should assure that their properties will be loaded from the database when needed.
	 */
	@Test
	public void testContributeClients() {
		List<String> allowedNames = Arrays.asList("SamlOne", "SamlTwo", "SamlThree");
		
		Set<Client> contributedClients = contributorUnderTest.contributeClients();
		assertNotNull(contributedClients);
		assertEquals(3, contributedClients.size());
		
		for (Client c: contributedClients) {
			assertTrue(allowedNames.contains(c.getName()));
			assertTrue(c instanceof SAML2Client);
			assertTrue(((SAML2Client)c).getConfiguration() instanceof DatabaseSAML2ClientConfiguration);
		}
	}

}
