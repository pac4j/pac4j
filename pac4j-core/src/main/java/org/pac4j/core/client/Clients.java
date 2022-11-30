package org.pac4j.core.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.util.*;

/**
 * <p>This class is made to group multiple clients, generally on one callback url.</p>
 *
 * <p>Clients can be changed at any time.</p>
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
@Slf4j
@Getter
@Setter
@ToString
public class Clients extends InitializableObject {

    private volatile List<Client> clients = new ArrayList<>();

    private Map<String, Client> clientsMap;

    private volatile Integer oldClientsHash;

    private String callbackUrl;

    private AjaxRequestResolver ajaxRequestResolver;

    private UrlResolver urlResolver;

    private CallbackUrlResolver callbackUrlResolver;

    private List<AuthorizationGenerator> authorizationGenerators = new ArrayList<>();

    private String defaultSecurityClients;

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

    public Clients(final List<Client> clients) {
        setClients(clients);
    }

    public Clients(final Client... clients) {
        setClients(clients);
    }

    @Override
    protected boolean shouldInitialize(final boolean forceReinit) {
        if (forceReinit) {
            return true;
        }

        return oldClientsHash == null || oldClientsHash.intValue() != clients.hashCode();
    }

    /**
     * Populate the resolvers, callback URL and authz generators in the Client
     * if defined in Clients and not already in the Client itself. And check the client name.
     */
    @Override
    protected void internalInit(final boolean forceReinit) {
        clientsMap = new HashMap<>();
        for (val client : this.clients) {
            val name = client.getName();
            CommonHelper.assertNotBlank("name", name);
            val lowerTrimmedName = name.toLowerCase().trim();
            if (clientsMap.containsKey(lowerTrimmedName)) {
                throw new TechnicalException("Duplicate name in clients: " + name);
            }
            clientsMap.put(lowerTrimmedName, client);
            if (client instanceof IndirectClient indirectClient) {
                if (this.callbackUrl != null && indirectClient.getCallbackUrl() == null) {
                    indirectClient.setCallbackUrl(this.callbackUrl);
                }
                if (this.urlResolver != null && indirectClient.getUrlResolver() == null) {
                    indirectClient.setUrlResolver(this.urlResolver);
                }
                if (this.callbackUrlResolver != null && indirectClient.getCallbackUrlResolver() == null) {
                    indirectClient.setCallbackUrlResolver(this.callbackUrlResolver);
                }
                if (this.ajaxRequestResolver != null && indirectClient.getAjaxRequestResolver() == null) {
                    indirectClient.setAjaxRequestResolver(this.ajaxRequestResolver);
                }
            }
            val baseClient = (BaseClient) client;
            if (!authorizationGenerators.isEmpty()) {
                baseClient.addAuthorizationGenerators(this.authorizationGenerators);
            }
        }
        this.oldClientsHash = this.clients.hashCode();
    }

    /**
     * Return the right client according to the specific name.
     *
     * @param name name of the client
     * @return the right client
     */
    public Optional<Client> findClient(final String name) {
        CommonHelper.assertNotBlank("name", name);
        init();

        val foundClient = clientsMap.get(name.toLowerCase().trim());
        LOGGER.debug("Found client: {} for name: {}", foundClient, name);
        return Optional.ofNullable(foundClient);
    }

    /**
     * Find all the clients (initialized).
     *
     * @return all the clients (initialized)
     */
    public List<Client> findAllClients() {
        init();

        return getClients();
    }

    public void addClient(final Client client) {
        this.clients.add(client);
    }

    public void setClients(final List<Client> clients) {
        CommonHelper.assertNotNull("clients", clients);
        this.clients = clients;
    }

    public void setClients(final Client... clients) {
        CommonHelper.assertNotNull("clients", clients);
        setClients(new ArrayList<>(Arrays.asList(clients)));
    }

    public void setAuthorizationGenerators(final List<AuthorizationGenerator> authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators = authorizationGenerators;
    }

    public void setAuthorizationGenerators(final AuthorizationGenerator... authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators = Arrays.asList(authorizationGenerators);
    }

    public void setAuthorizationGenerator(final AuthorizationGenerator authorizationGenerator) {
        addAuthorizationGenerator(authorizationGenerator);
    }

    public void addAuthorizationGenerator(final AuthorizationGenerator authorizationGenerator) {
        CommonHelper.assertNotNull("authorizationGenerator", authorizationGenerator);
        this.authorizationGenerators.add(authorizationGenerator);
    }
}
