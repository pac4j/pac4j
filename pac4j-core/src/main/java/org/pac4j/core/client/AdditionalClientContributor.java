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
package org.pac4j.core.client;

import java.util.Set;


/**
 * Contributes clients to {@link Clients}, in addition to the clients that are defined statically in the constructor or set via a setter.
 * 
 * A contributor should be used if it is not know at build time what clients will exist, most likely because they are defined in a changeable way.
 * 
 * @author jkacer
 * @since 1.9.0
 */
public interface AdditionalClientContributor {

	/**
	 * Provides the contributor's name. Just for logging purposes.
	 * 
	 * @return The contributor's name.
	 */
	public String getContributorName();

	
	/**
	 * Contributes clients.
	 * 
	 * @return A set of clients of any type. Should never return {@code null} but an empty set, if there are no clients to be contributed.
	 */
	public Set<Client> contributeClients();
	
}
