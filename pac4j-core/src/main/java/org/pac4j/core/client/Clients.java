package org.pac4j.core.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

/**
 * <p>This class is made to group multiple clients using a specific parameter to distinguish them, generally on one
 * callback url.</p>
 * <p>The {@link #init()} method is used to initialize the callback urls of the clients from the callback url of the
 * clients group if empty and a specific parameter added to define the client targeted. It is implicitly called by the
 * "finders" methods and doesn't need to be called explicitly.</p>
 * <p>The {@link #findClient(WebContext)}, {@link #findClient(String)} or {@link #findClient(Class)} methods must be called
 * to find the right client according to the input context or type. The {@link #findAllClients()} method returns all the
 * clients.</p>
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public class Clients extends InitializableObject {

    public final static String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    private String clientNameParameter = DEFAULT_CLIENT_NAME_PARAMETER;

    private List<Client> clients;
    
    private String callbackUrl = null;


    public Clients() {
    }

    public Clients(final String callbackUrl, final List<Client> clients) {
        setCallbackUrl(callbackUrl);
        setClients(clients);
    }

    public Clients(final String callbackUrl, final Client... clients) {
        setCallbackUrl(callbackUrl);
        setClients(clients);
    }

    public Clients(final String callbackUrl, final Client client) {
        setCallbackUrl(callbackUrl);
        setClients(Arrays.asList(client));
    }

    public Clients(final List<Client> clients) {
        setClients(clients);
    }

    public Clients(final Client... clients) {
        setClients(clients);
    }

    public Clients(final Client client) {
        setClients(Arrays.asList(client));
    }

    
    /**
     * Initialize all clients by computing callback urls if necessary.
     */
    @Override
    protected void internalInit() {
        final HashSet<String> names = new HashSet<>();
        final List<Client> loadedClients = loadClientsInternal();
        CommonHelper.assertNotNull("clients", loadedClients); // Check postponed after loading
        
        if (loadedClients != null) {
	        for (final Client client : loadedClients) {
	            final String name = client.getName();
	            final String lowerName = name.toLowerCase();
	            if (names.contains(lowerName)) {
	                throw new TechnicalException("Duplicate name in clients: " + name);
	            }
	            names.add(name);
	            updateCallbackUrlOfIndirectClient(client);
	        }
        }
    }

    
    /**
     * Sets a client's Callback URL, if not already set. If requested, the "client_name" parameter will also be a part of the URL.
     * 
     * @param client A client.
     */
    protected void updateCallbackUrlOfIndirectClient(final Client client) {
    	if (CommonHelper.isNotBlank(this.callbackUrl) && client instanceof IndirectClient) {
            final IndirectClient indirectClient = (IndirectClient) client;
            String indirectClientCallbackUrl = indirectClient.getCallbackUrl();
            // no callback url defined for the client -> set it with the group callback url
            if (indirectClientCallbackUrl == null) {
                indirectClient.setCallbackUrl(this.callbackUrl);
                indirectClientCallbackUrl = this.callbackUrl;
            }
            // if the "client_name" parameter is not already part of the callback url, add it unless the client
            // has indicated to not include it.
            if (indirectClient.isIncludeClientNameInCallbackUrl() &&
                    indirectClientCallbackUrl.indexOf(this.clientNameParameter + "=") < 0) {
                indirectClient.setCallbackUrl(CommonHelper.addParameter(indirectClientCallbackUrl, this.clientNameParameter, client.getName()));
            }
        }
    }

    /**
     * Return the right client according to the web context.
     * 
     * @param context web context
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
     * @param name name of the client
     * @return the right client
     */
    public Client findClient(final String name) {
        for (final Client client : getClients()) {
            if (CommonHelper.areEqualsIgnoreCaseAndTrim(name, client.getName())) {
                return client;
            }
        }
        final String message = "No client found for name: " + name;
        throw new TechnicalException(message);
    }

    /**
     * Return the right client according to the specific class.
     *
     * @param clazz class of the client
     * @param <C> the kind of client
     * @return the right client
     */
    @SuppressWarnings("unchecked")
    public <C extends Client> C findClient(final Class<C> clazz) {
        if (clazz != null) {
          for (final Client client : getClients()) {
            if (clazz.isAssignableFrom(client.getClass())) {
                return (C) client;
            }
          }
        }
        final String message = "No client found for class: " + clazz;
        throw new TechnicalException(message);
    }

    /**
     * Find all the clients.
     *
     * @return all the clients
     */
    public List<Client> findAllClients() {
        return getClients();
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

    public void setClients(final List<Client> clients) {
        this.clients = clients;
    }

    public void setClients(final Client... clients) {
        this.clients = Arrays.asList(clients);
    }
    
    /**
     * Returns initialized clients.
     * 
     * @return A list of clients ready to be used.
     */
    public List<Client> getClients() {
    	init();
    	if (this.clients == null) {
    		return Collections.emptyList();
    	} else {
    		return this.clients;
    	}
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "clientNameParameter",
                this.clientNameParameter, "clients", getClients());
    }

    
	/**
	 * An internal method that loads client definitions, if needed. The loaded clients should be returned as the result of this method and
	 * also set into an internal variable that is later returned by {@link #getClients()}. These two methods should always be consistent
	 * with each other.
	 * 
	 * @return A list of clients ready to be used.
	 * 
	 * @see #getClients()
	 */
    protected List<Client> loadClientsInternal() {
    	// Nothing is needed to be done with this.clients. They are statically defined.
    	return this.clients;
    }
    
}
