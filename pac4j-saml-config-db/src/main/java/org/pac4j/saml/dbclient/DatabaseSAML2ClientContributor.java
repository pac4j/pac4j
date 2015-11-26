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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pac4j.core.client.AdditionalClientContributor;
import org.pac4j.core.client.Client;
import org.pac4j.saml.client.AbstractSAML2ClientConfiguration;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Client contributor that contributes SAML2 clients, based on their definition in a relational database.
 * 
 * @author jkacer
 * @since 1.9.0
 */
public class DatabaseSAML2ClientContributor implements AdditionalClientContributor {

    /** SLF4J logger. */
    private final Logger logger = LoggerFactory.getLogger(DatabaseSAML2ClientContributor.class);

    /** DAO reading definitions of SAML clients from a database. */
    private final SamlClientDao samlClientDao;

    
    // ------------------------------------------------------------------------------------------------------------------------------------
    
    
	/**
	 * Creates a database SAML2 contributor.
	 * 
	 * @param samlClientDao
	 *            A DAO to read client definitions from the database.
	 */
	public DatabaseSAML2ClientContributor(final SamlClientDao samlClientDao) {
		super();
		if (samlClientDao == null) {
			throw new IllegalArgumentException("The SAML Client DAO cannot be null.");
		}
		this.samlClientDao = samlClientDao;
	}
    
    
	/* (non-Javadoc)
	 * @see org.pac4j.core.client.AdditionalClientContributor#getContributorName()
	 */
	@Override
	public String getContributorName() {
		return "Database SAML2 Client Contributor";
	}


	/**
	 * {@inheritDoc}
	 * 
	 * Creates a new SAML2 client for each found record. The configuration associated with the client is not fully initialized yet. It will
	 * be fully loaded using its {@code init()} method the client is actually used.
	 * 
	 * @return A set of SAML2 clients, partially initialized.
	 * 
	 * @see org.pac4j.core.client.AdditionalClientContributor#contributeClients()
	 */
	@Override
	public Set<Client> contributeClients() {
		
		final List<String> clientNames = samlClientDao.loadClientNames();
		final Set<Client> clients = new HashSet<>();
		
		for (final String name: clientNames) {
			final AbstractSAML2ClientConfiguration configuration = new DatabaseSAML2ClientConfiguration(samlClientDao);
			final SAML2Client client = new SAML2Client(configuration);
			client.setName(name);
			clients.add(client);
		}
		
		logger.info("Contributing {} dynamically loaded SAML clients.", clients.size());
		return clients;
	}

}
