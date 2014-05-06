/*
  Copyright 2012 - 2014 Jerome Leleu

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
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is made to group multiple clients using a specific parameter to distinguish them, generally on one
 * callback url.
 * <p />
 * The {@link #init()} method is used to initialize the callback urls of the clients from the callback url of the
 * clients group if empty and a specific parameter added to define the client targeted. It is implicitly called by the
 * "finders" methods and doesn't need to be called explicitly.
 * <p />
 * The {@link #findClient(WebContext)}, {@link #findClient(String)} or {@link #findClient(Class)} methods must be called
 * to find the right client according to the input context or type. The {@link #findAllClients()} method returns all the
 * clients.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class Clients extends InitializableObject {

    private static final Logger logger = LoggerFactory.getLogger(Clients.class);

    public final static String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    private String clientNameParameter = DEFAULT_CLIENT_NAME_PARAMETER;

    private List<Client> clients;

    private String callbackUrl;

    public Clients() {
    }

    public Clients(final String callbackUrl, final List<Client> clients) {
        setCallbackUrl(callbackUrl);
        setClientsList(clients);
    }

    public Clients(final String callbackUrl, final Client... clients) {
        setCallbackUrl(callbackUrl);
        setClients(clients);
    }

    /**
     * Initialize all clients by computing callback urls.
     */
    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("clients", this.clients);
        for (final Client client : this.clients) {
            final BaseClient baseClient = (BaseClient) client;
            final String baseClientCallbackUrl = baseClient.getCallbackUrl();
            // no callback url defined for the client -> set it with the group callback url + the "clientName" parameter
            if (baseClientCallbackUrl == null) {
                baseClient.setCallbackUrl(CommonHelper.addParameter(this.callbackUrl, this.clientNameParameter,
                        baseClient.getName()));
                // a callback url is already defined for the client without the "clientName" parameter -> just add it
            } else if (baseClientCallbackUrl.indexOf(this.clientNameParameter + "=") < 0) {
                baseClient.setCallbackUrl(CommonHelper.addParameter(baseClientCallbackUrl, this.clientNameParameter,
                        baseClient.getName()));
            }
        }
    }

    /**
     * Return the right client according to the web context.
     * 
     * @param context
     * @return the right client
     */
    public Client findClient(final WebContext context) {
        final String name = context.getRequestParameter(this.clientNameParameter);
        CommonHelper.assertNotBlank("name", name);
        return findClient(name);
    }

    /**
     * Return the right client according to the specific name.
     * 
     * @param name
     * @return the right client
     */
    public Client findClient(final String name) {
        init();
        for (final Client client : this.clients) {
            if (CommonHelper.areEquals(name, client.getName())) {
                return client;
            }
        }
        final String message = "No client found for name: " + name;
        logger.error(message);
        throw new TechnicalException(message);
    }

    /**
     * Return the right client according to the specific class.
     *
     * @param class
     * @param C the client class
     * @return the right client
     */
    public <C extends Client> C findClient(final Class<C> clazz) {
        init();
        for (final Client client : this.clients) {
            if (client.getClass().equals(clazz)) {
                return (C) client;
            }
        }
        final String message = "No client found for class: " + clazz;
        logger.error(message);
        throw new TechnicalException(message);
    }

    /**
     * Find all the clients.
     * 
     * @return all the clients
     */
    public List<Client> findAllClients() {
        init();
        return this.clients;
    }

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

    public void setClientsList(final List<Client> clients) {
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
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "clientTypeParameter",
                this.clientNameParameter, "clients", this.clients);
    }
}
