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

import java.util.ArrayList;
import java.util.List;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

/**
 * This class is made to group multiple clients on one callback url (using a specific parameter to distinguish them).
 * <p />
 * The {@link #init()} method is used to initialize the callback urls of the clients from the callback url of the clients group + a specific
 * parameter added to define the client targeted. It is implicitly called by the "finders" methods and doesn't need to be called explicitly.
 * <br/ >
 * The failure urls are also computed if necessary.
 * <p />
 * The {@link #findClient(WebContext)} or {@link #findClient(String)} methods must be called to find the right client according to the input
 * context or type. The {@link #findAllClients()} method returns all the clients.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class ClientsGroup extends InitializableObject {
    
    public final static String DEFAULT_CLIENT_TYPE_PARAMETER = "client_type";
    
    private String clientTypeParameter = DEFAULT_CLIENT_TYPE_PARAMETER;
    
    private List<Client> clients;
    
    private String callbackUrl;
    
    private String failureUrl;
    
    public ClientsGroup() {
    }
    
    public ClientsGroup(final String callbackUrl, final List<Client> clients) {
        setCallbackUrl(callbackUrl);
        setClients(clients);
    }
    
    public ClientsGroup(final String callbackUrl, final Client... clients) {
        setCallbackUrl(callbackUrl);
        setClients(clients);
    }
    
    public ClientsGroup(final String callbackUrl, final String failureUrl, final List<Client> clients) {
        this(callbackUrl, clients);
        setFailureUrl(failureUrl);
    }
    
    public ClientsGroup(final String callbackUrl, final String failureUrl, final Client... clients) {
        this(callbackUrl, clients);
        setFailureUrl(failureUrl);
    }
    
    /**
     * Initialize all clients by computing callback and failure urls.
     * 
     * @throws ClientException
     */
    @Override
    protected void internalInit() throws ClientException {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("clients", this.clients);
        for (final Client client : this.clients) {
            final BaseClient baseClient = (BaseClient) client;
            final String clientCallbackUrl = baseClient.getCallbackUrl();
            if (clientCallbackUrl == null || clientCallbackUrl.indexOf(this.clientTypeParameter + "=") < 0) {
                baseClient.setCallbackUrl(CommonHelper.addParameter(this.callbackUrl, this.clientTypeParameter,
                                                                    baseClient.getType()));
            }
        }
        if (CommonHelper.isNotBlank(this.failureUrl)) {
            for (final Client client : this.clients) {
                final BaseClient baseClient = (BaseClient) client;
                if (CommonHelper.isBlank(baseClient.getFailureUrl())) {
                    baseClient.setFailureUrl(this.failureUrl);
                }
            }
        }
    }
    
    /**
     * Return the right client according to the web context.
     * 
     * @param context
     * @return the right client
     * @throws ClientException
     */
    public Client findClient(final WebContext context) throws ClientException {
        final String type = context.getRequestParameter(this.clientTypeParameter);
        if (type != null) {
            return findClient(type);
        }
        return null;
    }
    
    /**
     * Return the right client according to the specific type.
     * 
     * @param type
     * @return the right client
     * @throws ClientException
     */
    public Client findClient(final String type) throws ClientException {
        init();
        for (final Client client : this.clients) {
            if (CommonHelper.areEquals(type, client.getType())) {
                return client;
            }
        }
        return null;
    }
    
    /**
     * Find all the clients.
     * 
     * @return all the clients
     * @throws ClientException
     */
    public List<Client> findAllClients() throws ClientException {
        init();
        return this.clients;
    }
    
    /**
     * This method built the group from just one client (copying the callback and failure urls).
     * 
     * @param client
     */
    public void buildFromOneClient(final Client client) {
        this.clients = new ArrayList<Client>();
        this.clients.add(client);
        final BaseClient baseClient = (BaseClient) client;
        this.callbackUrl = baseClient.getCallbackUrl();
        this.failureUrl = baseClient.getFailureUrl();
    }
    
    public void setClientTypeParameter(final String clientTypeParameter) {
        this.clientTypeParameter = clientTypeParameter;
    }
    
    public String getClientTypeParameter() {
        return this.clientTypeParameter;
    }
    
    public String getCallbackUrl() {
        return this.callbackUrl;
    }
    
    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public String getFailureUrl() {
        return this.failureUrl;
    }
    
    public void setFailureUrl(final String failureUrl) {
        this.failureUrl = failureUrl;
    }
    
    public void setClients(final List<Client> clients) {
        this.clients = clients;
    }
    
    public void setClients(final Client... clients) {
        this.clients = new ArrayList<Client>();
        for (final Client client : clients) {
            this.clients.add(client);
        }
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "failureUrl", this.failureUrl,
                                     "clientTypeParameter", this.clientTypeParameter, "clients", this.clients);
    }
}
