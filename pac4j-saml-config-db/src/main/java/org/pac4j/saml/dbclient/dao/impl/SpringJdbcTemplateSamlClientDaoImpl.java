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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.pac4j.saml.dbclient.DbLoadedSamlClientConfiguration;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>Implementation of {@link SamlClientDao} based on Spring JDBC Template.</p>
 * 
 * <p>It requires a table in a relational database with the following structure:</p>
 * <ul>
 * <li>CLIENT_NAME VARCHAR - PAC4J client name; unique</li>
 * <li>ENVIRONMENT VARCHAR2 - Application environment to which this client configuration applies</li>
 * <li>KEYSTORE_DATA BLOB - JKS binary data; replacement of a JKS file on disk</li>
 * <li>KEYSTORE_PASSWORD - Password for the JKS keystore</li>
 * <li>PRIVATE_KEY_PASSWORD VARCHAR2 - Password for a single alias (private key) in the JKS keystore</li>
 * <li>IDP_METADATA CLOB - IdP metadata in XML format</li>
 * <li>IDP_ENTITY_ID VARCHAR2 - IdP entity ID</li>
 * <li>SP_ENTITY_ID VARCHAR2 - SP entity ID</li>
 * <li>MAX_AUTH_LIFETIME NUMBER - Maximum authentication lifetime in seconds</li>
 * <li>DEST_BINDING_TYPE VARCHAR2 - Destination binding type as defined by SAML (POST or Redirect URIs)</li>
 * </ul>
 * 
 * <p>Example of table definition in Oracle 10:</p>
 * 
 * <pre>
CREATE TABLE SAG_SAML_PAC4J_CLIENT_CFG 
(
  SSPCC_ID NUMBER NOT NULL,
  CLIENT_NAME VARCHAR2(200 CHAR) NOT NULL,
  ENVIRONMENT VARCHAR2(20 CHAR) NOT NULL,
  KEYSTORE_DATA BLOB NOT NULL,
  KEYSTORE_PASSWORD VARCHAR2(50 CHAR),
  PRIVATE_KEY_PASSWORD VARCHAR2(50 CHAR),
  IDP_METADATA CLOB NOT NULL,
  IDP_ENTITY_ID VARCHAR2(200 CHAR) NOT NULL,
  SP_ENTITY_ID VARCHAR2(200 CHAR) NOT NULL,
  MAX_AUTH_LIFETIME NUMBER DEFAULT 3600 NOT NULL,
  DEST_BINDING_TYPE VARCHAR2(200 CHAR),
  CONSTRAINT SAG_SAML_PAC4J_CLIENT_CFG_PK PRIMARY KEY (SSPCC_ID) ENABLE,
  CONSTRAINT SAG_SAML_PAC4J_CLIENT_CFG_UK1 UNIQUE (CLIENT_NAME,ENVIRONMENT) ENABLE,
  CONSTRAINT SAG_SAML_PAC4J_CLIENT_CF_CHK1 CHECK (MAX_AUTH_LIFETIME &gt; 0) ENABLE
);
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.SSPCC_ID IS 'Primary key';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.CLIENT_NAME IS 'PAC4J client name; unique';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.ENVIRONMENT IS 'Application environment to which this client config applies';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.KEYSTORE_DATA IS 'JKS binary data; replacement of a JKS file on disk';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.KEYSTORE_PASSWORD IS 'Password for the JKS keystore';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.PRIVATE_KEY_PASSWORD IS 'Password for a single alias (private key) in the JKS keystore';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.IDP_METADATA IS 'IdP metadata in XML format';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.IDP_ENTITY_ID IS 'IdP entity ID';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.SP_ENTITY_ID IS 'SP entity ID';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.MAX_AUTH_LIFETIME IS 'Maximum authentication lifetime in seconds';
COMMENT ON COLUMN SAG_SAML_PAC4J_CLIENT_CFG.DEST_BINDING_TYPE IS 'Destination binding type as defined by SAML (POST or Redirect URIs)';
 * </pre>
 *
 * <p>The table name must be configured; there is no default value.</p>
 * 
 * <p>Another property to be configured is the so-called "environment". Using the environment property, you can distinguish configuration
 * records for multiple environments where your application runs and still have the complete configuration in a single table and database.
 * Example: You can have environments "Development", "Test" and "Production".</p>
 * 
 * @author jkacer
 */
public class SpringJdbcTemplateSamlClientDaoImpl implements SamlClientDao {

	/** SQL text of the query to select all existing clients. */
	private static final String SELECT_ALL_CLIENTS_SQL_TEXT = "select Client_Name, Keystore_Data, Keystore_Password, Private_Key_Password, IdP_Metadata, IdP_Entity_ID, SP_Entity_ID, Max_Auth_Lifetime, Dest_Binding_Type from %s where Environment = ?";

	/** Row mapper to read configuration rows and convert them to objects. */
	private final SamlClientConfigurationRowMapper rowMapper;

	/** A Spring JDBC template. */
	private final JdbcTemplate template;
	
	/** SQL text of the query to select all existing clients after filling in the table name. */
	private final String selectAllClientsSqlText;
	
	/** Parameters to the query selecting all clients: environment. */
	private final Object[] selectAllClientsParameters;
	/** Parameter types to the query selecting all clients: environment. */
	private final int[] selectAllClientsParameterTypes;

	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Creates a new DAO.
	 * 
	 * @param dataSource
	 *            A data source.
	 * @param lobHandler
	 *            LOB handler able to read BLOBs and CLOBs. As of Oracle 10.2, DefaultLobHandler should work with standard setup out of the
	 *            box. No need for OracleLobHandler.
	 * @param tableName
	 *            Name of the table containing configuration records.
	 * @param environment
	 *            Environment name; to distinguish records for different environments in a single table.
	 */
	public SpringJdbcTemplateSamlClientDaoImpl(final DataSource dataSource, final LobHandler lobHandler, final String tableName, final String environment) {
		super();
		this.rowMapper = new SamlClientConfigurationRowMapper(lobHandler);
		this.template = new JdbcTemplate(dataSource);
		this.selectAllClientsSqlText = String.format(SELECT_ALL_CLIENTS_SQL_TEXT, tableName);
		this.selectAllClientsParameters = new Object[] {environment};
		this.selectAllClientsParameterTypes = new int[] {Types.VARCHAR};
	}


	/* (non-Javadoc)
	 * @see org.pac4j.saml.dbclient.dao.api.SamlClientDao#loadAllClients()
	 */
	@Override
	@Transactional(readOnly=true)
	public List<DbLoadedSamlClientConfiguration> loadAllClients() {
		List<DbLoadedSamlClientConfiguration> configurations = template.query(selectAllClientsSqlText, selectAllClientsParameters, selectAllClientsParameterTypes, rowMapper);
		return configurations;
	}
	
	
	/**
	 * Spring JDBC row mapper to read configuration rows and convert them to objects of type {@link DbLoadedSamlClientConfiguration}.
	 * 
	 * @author jkacer
	 */
	private static class SamlClientConfigurationRowMapper implements RowMapper<DbLoadedSamlClientConfiguration> {

		/**
		 * LOB handler able to read BLOBs and CLOBs. As of Oracle 10.2, DefaultLobHandler should work with standard setup out of the box. No
		 * need for OracleLobHandler.
		 */
		private final LobHandler lobHandler;

		
		/**
		 * Creates a new row mapper.
		 * 
		 * @param lobHandler
		 *            LOB handler able to read BLOBs and CLOBs.
		 */
		public SamlClientConfigurationRowMapper(final LobHandler lobHandler) {
			super();
			this.lobHandler = lobHandler;
		}

		
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public DbLoadedSamlClientConfiguration mapRow(ResultSet rs, int rowNumber) throws SQLException {
			String clientName = rs.getString("Client_Name");
			byte[] keystoreBinaryData = lobHandler.getBlobAsBytes(rs, "Keystore_Data");
			String keystorePassword = rs.getString("Keystore_Password");
			String privateKeyPassword = rs.getString("Private_Key_Password");
			String identityProviderMetadata = lobHandler.getClobAsString(rs, "IdP_Metadata");
			String identityProviderEntityId = rs.getString("IdP_Entity_ID");
			String serviceProviderEntityId = rs.getString("SP_Entity_ID");
			int maximumAuthenticationLifetime = rs.getInt("Max_Auth_Lifetime");
			String destinationBindingType = rs.getString("Dest_Binding_Type");
			
			DbLoadedSamlClientConfiguration config = new DbLoadedSamlClientConfiguration();
			config.setKeystoreBinaryData(keystoreBinaryData);
			config.setKeystorePassword(keystorePassword);
			config.setPrivateKeyPassword(privateKeyPassword);
			config.setIdentityProviderMetadata(identityProviderMetadata);
			config.setIdentityProviderEntityId(identityProviderEntityId);
			config.setServiceProviderEntityId(serviceProviderEntityId);
			config.setMaximumAuthenticationLifetime(maximumAuthenticationLifetime);
			config.setDestinationBindingType(destinationBindingType);
			config.setClientName(clientName);
			
			return config;
		}

	}
	
}
