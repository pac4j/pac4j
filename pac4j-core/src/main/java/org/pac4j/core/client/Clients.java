package org.pac4j.core.client;

import java.util.*;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is made to group multiple clients, generally on one callback url.</p>
 *
 * <p>The {@link #init()} method is used to initialize the clients with the general values: the callback URL, the AJAX resolver,
 * the URL resolver, the callback URL resolver and the authorization generators.</p>
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings({ "unchecked" })
public class Clients extends InitializableObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(Clients.class);

    private List<Client> clients;

    private Map<String, Client> _clients;

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

    public Clients(final String callbackUrl, final Client client) {
        setCallbackUrl(callbackUrl);
        setClients(Collections.singletonList(client));
    }

    public Clients(final List<Client> clients) {
        setClients(clients);
    }

    public Clients(final Client... clients) {
        setClients(clients);
    }

    public Clients(final Client client) {
        setClients(Collections.singletonList(client));
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("clients", getClients());
        _clients = new HashMap<>();
        for (final Client client : getClients()) {
            final String name = client.getName();
            final String lowerTrimmedName = name.toLowerCase().trim();
            if (_clients.containsKey(lowerTrimmedName)) {
                throw new TechnicalException("Duplicate name in clients: " + name);
            }
            _clients.put(lowerTrimmedName, client);
            if (client instanceof IndirectClient) {
                updateIndirectClient((IndirectClient) client);
            }
            final BaseClient baseClient = (BaseClient) client;
            if (!authorizationGenerators.isEmpty()) {
                baseClient.addAuthorizationGenerators(this.authorizationGenerators);
            }
        }
    }


    /**
     * Setup the indirect client.
     *
     * @param client the indirect client
     */
    protected void updateIndirectClient(final IndirectClient client) {
        if (this.callbackUrl != null && client.getCallbackUrl() ==  null) {
            client.setCallbackUrl(this.callbackUrl);
        }
        if (this.urlResolver != null && client.getUrlResolver() == null) {
            client.setUrlResolver(this.urlResolver);
        }
        if (this.callbackUrlResolver != null && client.getCallbackUrlResolver() == null) {
            client.setCallbackUrlResolver(this.callbackUrlResolver);
        }
        if (this.ajaxRequestResolver != null && client.getAjaxRequestResolver() == null) {
            client.setAjaxRequestResolver(this.ajaxRequestResolver);
        }
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
        final String lowerTrimmedName = name.toLowerCase().trim();
        final Client client = _clients.get(lowerTrimmedName);
        if (client != null) {
            return Optional.of(client);
        }
        LOGGER.debug("No client found for name: {}", name);
        return Optional.empty();
    }

    /**
     * Return the right client according to the specific class.
     *
     * @param clazz class of the client
     * @param <C> the kind of client
     * @return the right client
     */
    @SuppressWarnings("unchecked")
    public <C extends Client> Optional<C> findClient(final Class<C> clazz) {
        CommonHelper.assertNotNull("clazz", clazz);
        init();
        if (clazz != null) {
            for (final Client client : getClients()) {
                if (clazz.isAssignableFrom(client.getClass())) {
                    return Optional.of((C) client);
                }
            }
        }
        LOGGER.debug("No client found for class: {}", clazz);
        return Optional.empty();
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

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }

    public CallbackUrlResolver getCallbackUrlResolver() {
        return callbackUrlResolver;
    }

    public void setCallbackUrlResolver(final CallbackUrlResolver callbackUrlResolver) {
        this.callbackUrlResolver = callbackUrlResolver;
    }

    public List<AuthorizationGenerator> getAuthorizationGenerators() {
        return this.authorizationGenerators;
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

    public String getDefaultSecurityClients() {
        return defaultSecurityClients;
    }

    public void setDefaultSecurityClients(final String defaultSecurityClients) {
        this.defaultSecurityClients = defaultSecurityClients;
    }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "callbackUrl", this.callbackUrl, "clients", getClients(),
                "ajaxRequestResolver", ajaxRequestResolver, "callbackUrlResolver", callbackUrlResolver,
                "authorizationGenerators", authorizationGenerators, "defaultSecurityClients", defaultSecurityClients,
                "urlResolver", this.urlResolver);
    }
}
