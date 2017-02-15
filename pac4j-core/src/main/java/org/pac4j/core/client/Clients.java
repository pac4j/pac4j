package org.pac4j.core.client;

import java.util.*;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.UrlResolver;
import org.pac4j.core.http.DefaultAjaxRequestResolver;
import org.pac4j.core.http.DefaultUrlResolver;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Clients extends InitializableObject {

    public final static String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    private String clientNameParameter = DEFAULT_CLIENT_NAME_PARAMETER;

    private List<Client> clients;

    private String callbackUrl = null;

	private Client defaultClient;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    private UrlResolver urlResolver = new DefaultUrlResolver();

    private List<AuthorizationGenerator> authorizationGenerators = new ArrayList<>();

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

    /**
     * Initialize all clients by computing callback urls if necessary.
     */
    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("clients", getClients());
        final HashSet<String> names = new HashSet<>();
        for (final Client client : getClients()) {
            final String name = client.getName();
            final String lowerName = name.toLowerCase();
            if (names.contains(lowerName)) {
                throw new TechnicalException("Duplicate name in clients: " + name);
            }
            names.add(lowerName);
            if (client instanceof IndirectClient) {
                updateCallbackUrlOfIndirectClient((IndirectClient) client);
            }
            final BaseClient baseClient = (BaseClient) client;
            if (!authorizationGenerators.isEmpty()) {
                baseClient.addAuthorizationGenerators(this.authorizationGenerators);
            }
        }
    }

    
    /**
     * Sets a client's Callback URL, if not already set. If requested, the "client_name" parameter will also be a part of the URL.
     * 
     * @param indirectClient A client.
     */
    protected void updateCallbackUrlOfIndirectClient(final IndirectClient indirectClient) {
        String indirectClientCallbackUrl = indirectClient.getCallbackUrl();
        // no callback url defined for the client but a group callback one -> set it with the group callback url
        if (CommonHelper.isNotBlank(this.callbackUrl) && indirectClientCallbackUrl == null) {
            indirectClient.setCallbackUrl(this.callbackUrl);
            indirectClientCallbackUrl = this.callbackUrl;
        }
        // if the "client_name" parameter is not already part of the client callback url, add it unless the client has indicated to not include it.
        if (indirectClient.isIncludeClientNameInCallbackUrl() && indirectClientCallbackUrl != null && !indirectClientCallbackUrl.contains(this.clientNameParameter + "=")) {
            indirectClient.setCallbackUrl(CommonHelper.addParameter(indirectClientCallbackUrl, this.clientNameParameter, indirectClient.getName()));
        }
        final AjaxRequestResolver clientAjaxRequestResolver = indirectClient.getAjaxRequestResolver();
        if (ajaxRequestResolver != null && (clientAjaxRequestResolver == null || clientAjaxRequestResolver instanceof DefaultAjaxRequestResolver)) {
            indirectClient.setAjaxRequestResolver(ajaxRequestResolver);
        }
        final UrlResolver clientUrlResolver = indirectClient.getUrlResolver();
        if (urlResolver != null && (clientUrlResolver == null || clientUrlResolver instanceof DefaultUrlResolver)) {
            indirectClient.setUrlResolver(this.urlResolver);
        }
    }

    
    /**
     * Return the right client according to the web context.
     * 
     * @param context web context
     * @return the right client
     */
    public Client findClient(final WebContext context) {
        init();
        final String name = context.getRequestParameter(this.clientNameParameter);
		if (name == null && defaultClient != null) {
			return defaultClient;
		}
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

    public void setDefaultClient(final Client defaultClient) {
        this.defaultClient = defaultClient;
    }

    public Client getDefaultClient() {
        return defaultClient;
    }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
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

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "clientNameParameter",
                this.clientNameParameter, "clients", getClients(), "defaultClient", defaultClient, "ajaxRequestResolver", ajaxRequestResolver,
                "urlResolver", urlResolver, "authorizationGenerators", authorizationGenerators);
    }
}
