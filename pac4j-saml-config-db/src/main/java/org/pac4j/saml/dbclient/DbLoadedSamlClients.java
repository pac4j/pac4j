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
import java.util.Arrays;
import java.util.List;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An extension to base {@link Clients}, which works also with clients whose definition is stored in a database. 
 *
 * @author jkacer
 * @since 1.9.0
 */
public class DbLoadedSamlClients extends Clients {

    /** SLF4J logger. */
    private final Logger logger = LoggerFactory.getLogger(DbLoadedSamlClients.class);
    
    /** DAO reading definitions of SAML clients from a database. */
    private SamlClientDao samlClientDao;

    /** Clients set explicitly using a setter. */
    private List<Client> explicitClients;
    
    // ------------------------------------------------------------------------------------------------------------------------------------

    
	/**
	 * Creates a new client group with no explicit clients.
	 */
	public DbLoadedSamlClients() {
		super();
	}


	/**
	 * Creates a new client group with some explicit clients.
	 * 
	 * @param clients
	 *            The clients.
	 */
	public DbLoadedSamlClients(Client... clients) {
		super(clients);
	}


	/**
	 * Creates a new client group with a single explicit client.
	 * 
	 * @param client
	 *            The client.
	 */
	public DbLoadedSamlClients(Client client) {
		super(client);
	}


	/**
	 * Creates a new client group with some explicit clients.
	 * 
	 * @param clients
	 *            The clients.
	 */
	public DbLoadedSamlClients(List<Client> clients) {
		super(clients);
	}


	/**
	 * Creates a new client group with some explicit clients and a callback URL.
	 * 
	 * @param callbackUrl
	 *            The callback URL.
	 * @param clients
	 *            The clients.
	 */
	public DbLoadedSamlClients(String callbackUrl, Client... clients) {
		super(callbackUrl, clients);
	}


	/**
	 * Creates a new client group with some explicit clients and a callback URL.
	 * 
	 * @param callbackUrl
	 *            The callback URL.
	 * @param clients
	 *            The clients.
	 */
	public DbLoadedSamlClients(String callbackUrl, Client client) {
		super(callbackUrl, client);
	}


	/**
	 * Creates a new client group with some explicit clients and a callback URL.
	 *
	 * @param callbackUrl
	 *            The callback URL.
	 * @param clients
	 *            The clients.
	 */
	public DbLoadedSamlClients(String callbackUrl, List<Client> clients) {
		super(callbackUrl, clients);
	}


	/**
	 * Initializes all clients by loading them from the database.
	 * 
	 * @see org.pac4j.core.util.InitializableWebObject#internalInit(org.pac4j.core.context.WebContext)
	 */
	@Override
	protected void internalInit(final WebContext context) {
        // Remove all clients first if re-initialized
		List<Client> existingClients = super.getClients();
		if (existingClients == null) {
			existingClients = new ArrayList<Client>();
			setClients(existingClients);
		}
		existingClients.clear();
		
		// Then, include those explicitly set. Do not include thos provided by the DAO yet.
		existingClients.addAll(explicitClients);

		// Initialize like in the super class.
		super.internalInit(context);
        CommonHelper.assertNotNull("samlClientDao", this.samlClientDao);

        // And finally add those contributed by the DAO.
        // Load all defined client names and create a configuration and client for each of them. But do not initialize them yet.
        // Alternative 1: Initialize them completely here, then DbLoadedSamlClientConfiguration.internalInit() could be empty.
        // Alternative 2: Have an AdditionalClientContributor (or something named like that) in Clients and remove this class.
        logger.debug("Loading SAML client definition from the database...");
        List<String> namesOfDefinedSamlClients = samlClientDao.loadClientNames();
        for (String singleClientName: namesOfDefinedSamlClients) {
        	DbLoadedSamlClientConfiguration configuration = new DbLoadedSamlClientConfiguration(samlClientDao);
        	SAML2Client client = new SAML2Client(configuration);
        	client.setName(singleClientName);
        	existingClients.add(client);
        	updateCallbackUrlOfIndirectClient(client);
        }
        logger.debug("SAML clients loaded OK.");
	}


	// ------------------------------------------------------------------------------------------------------------------------------------

    
	public SamlClientDao getSamlClientDao() {
		return samlClientDao;
	}

	public void setSamlClientDao(SamlClientDao samlClientDao) {
		this.samlClientDao = samlClientDao;
	}

    @Deprecated
    public void setClientsList(final List<Client> clients) {
    	super.setClientsList(clients);
    	explicitClients = new ArrayList<Client>(clients);
    }

    public void setClients(final List<Client> clients) {
    	super.setClients(clients);
    	explicitClients = new ArrayList<Client>(clients);
    }

    public void setClients(final Client... clients) {
    	super.setClients(clients);
    	explicitClients = new ArrayList<Client>(Arrays.asList(clients));
    }
}
