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
package org.pac4j.saml.dbclient.dao.api;

import java.util.List;


/**
 * DAO to manipulate SAML Client Configurations stored in a data source.
 * 
 * @author jkacer
 * @since 1.9.0
 */
public interface SamlClientDao {

	/**
	 * Finds names of all existing clients.
	 * 
	 * @return A list of names.
	 */
	public List<String> loadClientNames();
	
	
	/**
	 * Loads all existing SAML Client Configurations.
	 * 
	 * @return A list of configurations.
	 */
	public List<DbLoadedSamlClientConfigurationDto> loadAllClients();
	
	
	/**
	 * Loads a single SAML Client Configuration.
	 * 
	 * @param clientName
	 *            Name of the client.
	 * 
	 * @return A single configuration for the desired client or {@code null} of no such configuration exists.
	 */
	public DbLoadedSamlClientConfigurationDto loadClient(String clientName);
	
}
