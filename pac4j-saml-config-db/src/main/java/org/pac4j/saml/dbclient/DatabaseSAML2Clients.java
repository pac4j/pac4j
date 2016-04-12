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

import java.util.ArrayList;
import java.util.List;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.saml.client.AbstractSAML2ClientConfiguration;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A special version of {@link Clients} that dynamically loads SAML2 Clients from a database. No other clients are supported.
 * 
 * @author jkacer
 * @since 1.9.0
 */
@SuppressWarnings("rawtypes")
public class DatabaseSAML2Clients extends Clients {

	/** SLF4J logger. */
    private final Logger logger = LoggerFactory.getLogger(DatabaseSAML2Clients.class);
	
    /** DAO reading definitions of SAML clients from a database. */
    private final SamlClientDao samlClientDao;

    /** Loaded clients. */
	private List<Client> dynamicallyLoadedClients;
	
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	

	/**
	 * Creates a new client group, based on a DAO loading client definitions.
	 * 
	 * @param samlClientDao
	 *            A DAO that loads definitions of SAML2 clients.
	 */
	public DatabaseSAML2Clients(SamlClientDao samlClientDao) {
		super();
		this.samlClientDao = samlClientDao;
		this.dynamicallyLoadedClients = new ArrayList<>();
	}

	
	
	/* (non-Javadoc)
	 * @see org.pac4j.core.client.Clients#getClients()
	 */
	@Override
	public List<Client> getClients() {
		init();
		return this.dynamicallyLoadedClients; // Never null, can be an empty list
	}

	
	/* (non-Javadoc)
	 * @see org.pac4j.core.client.Clients#loadClientsInternal()
	 */
	@Override
	protected List<Client> loadClientsInternal() {
		this.dynamicallyLoadedClients = new ArrayList<>();
		
		final List<String> clientNames = samlClientDao.loadClientNames();
		for (final String name: clientNames) {
			final AbstractSAML2ClientConfiguration configuration = new DatabaseSAML2ClientConfiguration(samlClientDao);
			final SAML2Client client = new SAML2Client(configuration);
			client.setName(name);
			this.dynamicallyLoadedClients.add(client);
		}
		
		logger.info("Dynamically loaded {} SAML clients.", this.dynamicallyLoadedClients.size());
		return this.dynamicallyLoadedClients;
	}


	@Override
	public void setClients(List<Client> clients) {
		throw new UnsupportedOperationException("Method setClients(List<Client> clients) is not implemented.");
	}

	@Override
	public void setClients(Client... clients) {
		throw new UnsupportedOperationException("Method setClients(Client... clients) is not implemented.");
	}

}
