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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pac4j.saml.dbclient.dao.api.DbLoadedSamlClientConfigurationDto;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;


/**
 * <p>Implementation of {@link SamlClientDao} providing very simple caching.</p>
 * 
 * <p>It bulk-loads all values just once, keeps them in memory forever, then returns the cached values on each call. It delegates bulk-reading
 * to another implementation of the same DAO type.</p>
 * 
 * @author jkacer
 * @since 1.9.0
 */
public class CachingSamlClientDaoImpl implements SamlClientDao {

	/** A DAO that performs actual reading. */
	private final SamlClientDao realDao;

	/** Cached configurations, read just once. */
	private Map<String, DbLoadedSamlClientConfigurationDto> cachedConfigurations;
	

	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Creates a new caching DAO.
	 * 
	 * @param realDao
	 *            A real DAO, which performs actual read operations.
	 */
	public CachingSamlClientDaoImpl(final SamlClientDao realDao) {
		super();
		if (realDao == null) {
			throw new IllegalArgumentException("Real DAO must not be null.");
		}
		this.realDao = realDao;
		this.cachedConfigurations = null;
	}

	
	/* (non-Javadoc)
	 * @see org.pac4j.saml.dbclient.dao.api.SamlClientDao#loadClientNames()
	 */
	@Override
	public List<String> loadClientNames() {
		checkAndLoadFromRealDao();
		Set<String> keys = cachedConfigurations.keySet();
		return new ArrayList<String>(keys);
	}
	

	/* (non-Javadoc)
	 * @see org.pac4j.saml.dbclient.dao.api.SamlClientDao#loadAllClients()
	 */
	@Override
	public List<DbLoadedSamlClientConfigurationDto> loadAllClients() {
		checkAndLoadFromRealDao();
		Collection<DbLoadedSamlClientConfigurationDto> values = cachedConfigurations.values();
		return new ArrayList<DbLoadedSamlClientConfigurationDto>(values);
	}

	
	/* (non-Javadoc)
	 * @see org.pac4j.saml.dbclient.dao.api.SamlClientDao#loadClient(java.lang.String)
	 */
	@Override
	public DbLoadedSamlClientConfigurationDto loadClient(String clientName) {
		checkAndLoadFromRealDao();
		return cachedConfigurations.get(clientName);
	}

	
	/**
	 * Atomically checks and loads all configurations from the real DAO, if they have not been read yet.
	 * 
	 * Assures that just one reading is done.
	 */
	private synchronized void checkAndLoadFromRealDao() {
		if (cachedConfigurations == null) {
			List<DbLoadedSamlClientConfigurationDto> allLoaded = realDao.loadAllClients();
			cachedConfigurations = new HashMap<String, DbLoadedSamlClientConfigurationDto>();
			for (DbLoadedSamlClientConfigurationDto single: allLoaded) {
				cachedConfigurations.put(single.getClientName(), single);
			}
		}
	}

}
