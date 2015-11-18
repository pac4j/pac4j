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
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * An alternative to {@link Clients}. Unlike {@link Clients}, this class works with clients whose definition is stored in a database. It is
 * limited to {@link DbLoadedSamlClient}s only, so clients of another type cannot be managed by this class.
 * </p>
 * 
 * <p>
 * This class is made to group multiple clients using a specific parameter to distinguish them, generally on one callback url.
 * </p>
 * 
 * <p>
 * The {@link #init(WebContext)} method is used to initialize the callback urls of the clients from the callback url of the clients group
 * if empty and a specific parameter added to define the client targeted. It is implicitly called by the "finders" methods and doesn't
 * need to be called explicitly.
 * </p>
 * 
 * <p>
 * The {@link #findClient(WebContext)}, {@link #findClient(WebContext, String)} or {@link #findClient(WebContext, Class)} methods must be
 * called to find the right client according to the input context or type. The {@link #findAllClients(WebContext)} method returns all the
 * clients.
 * </p>
 *
 * TODO: We could have a common class (maybe abstract) that would contain common code of this class and {@link Clients}. Most code is
 * duplicated. {@link Clients} cannot be extended because it is final.
 * 
 * @author jkacer
 * @since 1.9.0
 */
public class DbLoadedSamlClients extends InitializableWebObject {

	/** Default value for {@link #clientNameParameter}. */
    public final static String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    /** SLF4J logger. */
    private final Logger logger = LoggerFactory.getLogger(DbLoadedSamlClients.class);
	
    /** Common callback URL, applied to clients that do not have any callback set. */
    private String callbackUrl;
    
    /** List of managed SAML clients. */
    private final List<SAML2Client> clients;
    
    /** Name of the HTTP parameter holding client names. */
    private String clientNameParameter = DEFAULT_CLIENT_NAME_PARAMETER;
    
    /** DAO reading definitions of SAML clients from a database. */
    private SamlClientDao samlClientDao;

    
    // ------------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Creates a new client grouping.
	 * 
	 * @param callbackUrl
	 *            Common Callback URL. Will be used by all clients, unless they have their own value.
	 */
	public DbLoadedSamlClients(final String callbackUrl) {
		super();
		this.callbackUrl = callbackUrl;
		this.clients = new ArrayList<SAML2Client>();
	}
	
	
	/**
	 * Initializes all clients by loading them from the database.
	 * 
	 * @see org.pac4j.core.util.InitializableWebObject#internalInit(org.pac4j.core.context.WebContext)
	 */
	@Override
	protected void internalInit(final WebContext context) {

        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("clients", this.clients);
        CommonHelper.assertNotNull("samlClientDao", this.samlClientDao);

        // Remove all clients first if re-initialized
        this.clients.clear();
        
        logger.debug("Loading SAML client definition from the database...");
        
        // Load all defined client names and create a configuration and client for each of them.
        // But do not initialize them yet.
        // Alternative: Initialize them completely here, then DbLoadedSamlClientConfiguration.internalInit() could be empty.
        List<String> namesOfDefinedSamlClients = samlClientDao.loadClientNames();
        for (String singleClientName: namesOfDefinedSamlClients) {
        	DbLoadedSamlClientConfiguration configuration = new DbLoadedSamlClientConfiguration(samlClientDao);
        	SAML2Client client = new SAML2Client(configuration);
        	client.setName(singleClientName);
        	this.clients.add(client);
        }
        logger.debug("SAML clients loaded OK.");
        
        for (final SAML2Client client : this.clients) {
            String baseClientCallbackUrl = client.getCallbackUrl();
            // no callback url defined for the client -> set it with the group callback url
            if (baseClientCallbackUrl == null) {
                client.setCallbackUrl(this.callbackUrl);
                baseClientCallbackUrl = this.callbackUrl;
            }
            // if the "clientName" parameter is not already part of the callback url, add it unless the client has indicated to not include it.
        	if (client.isIncludeClientNameInCallbackUrl() && baseClientCallbackUrl.indexOf(this.clientNameParameter + "=") < 0) {
                client.setCallbackUrl(CommonHelper.addParameter(baseClientCallbackUrl, this.clientNameParameter, client.getName()));
            }
        }
	}

	
	// Copied from Clients
	public SAML2Client findClient(final WebContext context) {
        final String name = context.getRequestParameter(this.clientNameParameter);
        CommonHelper.assertNotBlank("name", name);
        return findClient(context, name);
    }

    
	// Copied from Clients
    public SAML2Client findClient(final WebContext context, final String name) {
        init(context);
        for (final SAML2Client client : this.clients) {
            if (CommonHelper.areEquals(name, client.getName())) {
                return client;
            }
        }
        final String message = "No client found for name: " + name;
        logger.error(message);
        throw new TechnicalException(message);
    }
    
    
	// Copied from Clients
    public <C extends Client> C findClient(final WebContext context, final Class<C> clazz) {
		init(context);
		if (clazz != null) {
			for (final Client client : this.clients) {
				if (clazz.isAssignableFrom(client.getClass())) {
					return (C) client;
				}
			}
		}
        final String message = "No client found for class: " + clazz;
        logger.error(message);
        throw new TechnicalException(message);
    }    

    
	// Copied from Clients
    public List<SAML2Client> findAllClients(final WebContext context) {
        init(context);
        return this.clients;
    }

    
	// Copied from Clients
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "clientTypeParameter", this.clientNameParameter, "clients", this.clients);
    }
    
    
    // ------------------------------------------------------------------------------------------------------------------------------------

    
    public String getClientNameParameter() {
        return this.clientNameParameter;
    }

    public void setClientNameParameter(final String clientNameParameter) {
        this.clientNameParameter = clientNameParameter;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

	public SamlClientDao getSamlClientDao() {
		return samlClientDao;
	}

	public void setSamlClientDao(SamlClientDao samlClientDao) {
		this.samlClientDao = samlClientDao;
	}

}
