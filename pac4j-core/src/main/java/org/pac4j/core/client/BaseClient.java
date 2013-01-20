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

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.ClientException;
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
 * <li>the type of the client is handled through the {@link #setType(String)} and {@link #getType()} methods</li>
 * <li>the failure url is handled through the {@link #setFailureUrl(String)}, {@link #getFailureUrl()} and
 * {@link #getFailureUrl(ClientException)} methods.</li>
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
    
    protected String callbackUrl;
    
    private String type;
    
    private String failureUrl;
    
    /**
     * Clone the current client.
     * 
     * @return the cloned client
     */
    @Override
    public BaseClient<C, U> clone() {
        final BaseClient<C, U> newClient = newClient();
        newClient.setCallbackUrl(this.callbackUrl);
        newClient.setType(this.type);
        newClient.setFailureUrl(this.failureUrl);
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
    
    /**
     * Get the failure url depending on the given exception.
     * 
     * @param exception
     * @return the failure url depending on the given exception
     */
    public String getFailureUrl(final ClientException exception) {
        return this.failureUrl;
    }
    
    public String getFailureUrl() {
        return this.failureUrl;
    }
    
    public void setFailureUrl(final String failureUrl) {
        this.failureUrl = failureUrl;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        if (CommonHelper.isBlank(this.type)) {
            return this.getClass().getSimpleName();
        }
        return this.type;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "type", this.type, "failureUrl",
                                     this.failureUrl);
    }
}
