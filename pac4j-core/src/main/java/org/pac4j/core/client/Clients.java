package org.pac4j.core.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(Clients.class);

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
        CommonHelper.assertNotNull("clients", getClients());
        final HashSet<String> names = new HashSet<>();
        for (final Client client : getClients()) {
            final String name = client.getName();
            if (names.contains(name)) {
                throw new TechnicalException("Duplicate name in clients: " + name);
            }
            names.add(name);
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
                    indirectClient.setCallbackUrl(CommonHelper.addParameter(indirectClientCallbackUrl, this.clientNameParameter,
                            name));
                }
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
        init();
        for (final Client client : getClients()) {
            if (CommonHelper.areEquals(name, client.getName())) {
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
        init();
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
        init();
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

    public List<Client> getClients() {
        return this.clients;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "clientNameParameter",
                this.clientNameParameter, "clients", getClients());
    }
}
