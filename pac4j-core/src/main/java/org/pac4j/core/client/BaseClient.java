/*
  Copyright 2012 -2013 Jerome Leleu

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
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the default implementation of a client (whatever the protocol). It has the core concepts :
 * <ul>
 * <li>the initialization process is handled by the {@link InitializableObject} inheritance, the {@link #internalInit()} must be implemented
 * in sub-classes</li>
 * <li>the cloning process is handled by the {@link #clone()} method, the {@link #newClient()} method must be implemented in sub-classes to
 * create a new instance</li>
 * <li>the callback url is handled through the {@link #setCallbackUrl(String)} and {@link #getCallbackUrl()} methods</li>
 * <li>the name of the client is handled through the {@link #setName(String)} and {@link #getName()} methods</li>
 * <li>the concept of "direct" redirection is defined through the {@link #isDirectRedirection()} method : if true, the
 * {@link #getRedirectionUrl(WebContext)} method will always return the redirection to the provider where as if it's false, the redirection
 * url will be the callback url with an additionnal parameter : {@link #NEEDS_CLIENT_REDIRECTION_PARAMETER} to require the redirection,
 * which will be handled <b>later</b> in the {@link #getCredentials(WebContext)} method.<br />
 * To force a direct redirection, the {@link #getRedirectionUrl(WebContext, boolean)} must be used with <code>true</code> for the
 * <code>forceDirectRedirection</code> parameter.</li>
 * </ul>
 * <p />
 * The {@link #init()} method must be called implicitly by the main methods of the {@link Client} interface, so that no explicit call is
 * required to initialize the client.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseClient<C extends Credentials, U extends CommonProfile> extends InitializableObject implements
    Client<C, U>, Cloneable {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseClient.class);
    
    public static final String NEEDS_CLIENT_REDIRECTION_PARAMETER = "needs_client_redirection";
    
    protected String callbackUrl;
    
    private String name;
    
    /**
     * Clone the current client.
     * 
     * @return the cloned client
     */
    @Override
    public BaseClient<C, U> clone() {
        final BaseClient<C, U> newClient = newClient();
        newClient.setCallbackUrl(this.callbackUrl);
        newClient.setName(this.name);
        return newClient;
    }
    
    /**
     * Create a new instance of the client.
     * 
     * @return A new instance of the client
     */
    protected abstract BaseClient<C, U> newClient();
    
    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public String getCallbackUrl() {
        return this.callbackUrl;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        if (CommonHelper.isBlank(this.name)) {
            return this.getClass().getSimpleName();
        }
        return this.name;
    }
    
    /**
     * Define if this client has a direct redirection.
     * 
     * @return if this client has a direct redirection
     */
    protected abstract boolean isDirectRedirection();
    
    public final String getRedirectionUrl(final WebContext context) throws TechnicalException {
        return getRedirectionUrl(context, false);
    }
    
    public final String getRedirectionUrl(final WebContext context, final boolean forceDirectRedirection)
        throws TechnicalException {
        init();
        // it's a direct redirection or force the redirection -> return the redirection url
        if (isDirectRedirection() || forceDirectRedirection) {
            return retrieveRedirectionUrl(context);
        } else {
            // return an intermediate url which is the callback url with a specific parameter requiring redirection
            return CommonHelper.addParameter(getCallbackUrl(), NEEDS_CLIENT_REDIRECTION_PARAMETER, "true");
        }
    }
    
    protected abstract String retrieveRedirectionUrl(final WebContext context) throws TechnicalException;
    
    public final C getCredentials(final WebContext context) throws TechnicalException, RequiresHttpAction {
        init();
        final String value = context.getRequestParameter(NEEDS_CLIENT_REDIRECTION_PARAMETER);
        // needs redirection -> return the redirection url
        if (CommonHelper.isNotBlank(value)) {
            throw RequiresHttpAction.redirect("Needs client redirection", context, retrieveRedirectionUrl(context));
        } else {
            // else get the credentials
            return retrieveCredentials(context);
        }
    }
    
    protected abstract C retrieveCredentials(final WebContext context) throws TechnicalException, RequiresHttpAction;
    
    public final U getUserProfile(final C credentials) throws TechnicalException {
        init();
        logger.debug("credentials : {}", credentials);
        if (credentials == null) {
            return null;
        }
        
        return retrieveUserProfile(credentials);
    }
    
    protected abstract U retrieveUserProfile(final C credentials) throws TechnicalException;
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", this.name,
                                     "isDirectRedirection", isDirectRedirection());
    }
}
