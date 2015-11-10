package org.pac4j.saml.dbclient.dao.api;

import java.util.List;

import org.pac4j.saml.dbclient.DbLoadedSamlClientConfiguration;


/**
 * DAO to manipulate SAML Client Configurations stored in a data source.
 * 
 * @author jkacer
 */
public interface SamlClientDao {

	/**
	 * Loads all existing SAML Client Configurations.
	 * 
	 * @return A list of configurations.
	 */
	public List<DbLoadedSamlClientConfiguration> loadAllClients();
	
}
