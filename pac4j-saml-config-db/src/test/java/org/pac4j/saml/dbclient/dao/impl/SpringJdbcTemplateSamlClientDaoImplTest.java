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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.saml.dbclient.DbLoadedSamlClientConfiguration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;


/**
 * Unit test of {@link SpringJdbcTemplateSamlClientDaoImpl}.
 * 
 * @author jkacer
 */
public class SpringJdbcTemplateSamlClientDaoImplTest {

	private static final String TABLE_NAME = "PAC4J_CFG";
	private static final String ENVIRONMENT = "UnitTest";
	
	private static final String PATH_TO_SCRIPT_CREATE_TABLE = "/org/pac4j/saml/dbclient/dao/impl/1_Create_Table.sql";
	private static final String PATH_TO_SCRIPT_FILL_DATA = "/org/pac4j/saml/dbclient/dao/impl/2_Fill_Data.sql";

	/** An instance of embedded database, also acting as a Data Source. */
	private EmbeddedDatabase db;
	/** The tested template. */
	private SpringJdbcTemplateSamlClientDaoImpl templateUnderTest;

	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	@Before
	public void initInMemoryDatabaseAndTemplate() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder.setType(EmbeddedDatabaseType.H2).addScript(PATH_TO_SCRIPT_CREATE_TABLE).addScript(PATH_TO_SCRIPT_FILL_DATA).build();
		LobHandler lobHandler = new DefaultLobHandler();
		templateUnderTest = new SpringJdbcTemplateSamlClientDaoImpl(db, lobHandler, TABLE_NAME, ENVIRONMENT);
	}

	
	@After
	public void shutDownInMemoryDatabase() throws Exception {
		if (db != null) {
			db.shutdown();
		}
	}

	
	/**
	 * Checks that loaded SAML Client Configurations for a particular environment correspond to the SQL script used to populate the
	 * database.
	 */
	@Test
	public void allClientConfigurationsMustBeLoaded() {
		String[] expectedNames = {"One", "Two", "Three", "Four", "Five"};
		// Expected bindings: In case of NULL, the configuration uses the default binding, which is HTTP POST.
		String[] expectedBindings = {SAMLConstants.SAML2_POST_BINDING_URI, "http://redirect", "http://post", SAMLConstants.SAML2_POST_BINDING_URI, "urn:binding"};
		
		List<DbLoadedSamlClientConfiguration> configurations = templateUnderTest.loadAllClients();
		assertNotNull(configurations);
		assertEquals(5, configurations.size());

		for (int i = 1; i <= 5; i++) {
			DbLoadedSamlClientConfiguration cfg = configurations.get(i-1);
			
			// Client name
			assertEquals(expectedNames[i-1], cfg.getClientName());
			
			// Keystore data
			byte[] keystoreData = cfg.getKeystoreBinaryData();
			byte[] expectedKeystoreData = createExpectedKeystoreData((byte) i);
			assertArrayEquals(expectedKeystoreData, keystoreData);

			// Keystore password
			assertEquals("KsPwd"+i, cfg.getKeystorePassword());

			// Private key password
			assertEquals("PrKeyPwd"+i, cfg.getPrivateKeyPassword());

			// IdP metadata
			assertEquals("CLOB-"+i, cfg.getIdentityProviderMetadata());
			
			// IdP entity ID
			assertEquals("urn:idp"+i, cfg.getIdentityProviderEntityId());
			
			// SP entity ID
			assertEquals("urn:sp"+i, cfg.getServiceProviderEntityId());

			// Maximum authentication lifetime
			assertEquals(i * 1000, cfg.getMaximumAuthenticationLifetime());

			// Destination binding type
			assertEquals(expectedBindings[i-1], cfg.getDestinationBindingType());
		}
	}

	
	private byte[] createExpectedKeystoreData(byte b) {
		byte[] result = new byte[5];
		Arrays.fill(result, b);
		return result;
	}

}
