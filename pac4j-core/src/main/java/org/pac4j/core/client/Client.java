/*
  Copyright 2012 - 2013 Jerome Leleu

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

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.profile.UserProfile;

/**
 * This interface represents a client (whatever the protocol).<br />
 * <br />
 * A client has a type accessible by the {@link #getType()} method.<br />
 * A client supports the authentication process and user profile retrieval through :<br />
 * <ul>
 * <li>the {@link #getRedirectionUrl(WebContext)} method to get the url where to redirect the user for authentication (at the provider)</li>
 * <li>the {@link #getCredentials(WebContext)} method to get the credentials (in the application) after the user has been successfully
 * authenticated at the provider</li>
 * <li>the {@link #getUserProfile(Credentials)} method to get the user profile from the credentials.</li>
 * </ul>
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface Client<C extends Credentials, U extends UserProfile> {
    
    /**
     * Get the type of the client.
     * 
     * @return the type of the client
     */
    public String getType();
    
    /**
     * Get the redirection url.
     * 
     * @param context
     * @return the redirection url
     * @throws ClientException
     */
    public String getRedirectionUrl(WebContext context) throws ClientException;
    
    /**
     * Get the credentials from the web context.
     * 
     * @param context
     * @return the credentials
     * @throws ClientException
     */
    public C getCredentials(WebContext context) throws ClientException;
    
    /**
     * Get the user profile from the credentials.
     * 
     * @param credentials
     * @return the user profile
     * @throws ClientException
     */
    public U getUserProfile(C credentials) throws ClientException;
}
