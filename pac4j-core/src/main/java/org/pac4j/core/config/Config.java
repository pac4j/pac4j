package org.pac4j.core.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.context.session.SessionStoreFactory;
import org.pac4j.core.engine.*;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.CommonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The configuration with clients, authorizers, matchers, etc.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Setter
@Getter
@With
@AllArgsConstructor
@Accessors(chain = true)
public class Config {

    private Clients clients = new Clients();

    private Map<String, Authorizer> authorizers = new HashMap<>();

    private Map<String, Matcher> matchers = new HashMap<>();

    private SecurityLogic securityLogic;

    private CallbackLogic callbackLogic;

    private LogoutLogic logoutLogic;

    private WebContextFactory webContextFactory;

    private SessionStoreFactory sessionStoreFactory;

    private ProfileManagerFactory profileManagerFactory;

    private HttpActionAdapter httpActionAdapter;

    /**
     * <p>Constructor for Config.</p>
     */
    public Config() {}

    /**
     * <p>Constructor for Config.</p>
     *
     * @param client a {@link Client} object
     */
    public Config(final Client client) {
        this.clients = new Clients(client);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param clients a {@link Clients} object
     */
    public Config(final Clients clients) {
        this.clients = clients;
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param clients a {@link List} object
     */
    public Config(final List<Client> clients) {
        this.clients = new Clients(clients);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param clients a {@link Client} object
     */
    public Config(final Client... clients) {
        this.clients = new Clients(clients);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param callbackUrl a {@link String} object
     * @param client a {@link Client} object
     */
    public Config(final String callbackUrl, final Client client) {
        this.clients = new Clients(callbackUrl, client);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param callbackUrl a {@link String} object
     * @param clients a {@link Client} object
     */
    public Config(final String callbackUrl, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param callbackUrl a {@link String} object
     * @param clients a {@link List} object
     */
    public Config(final String callbackUrl, final List<Client> clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param authorizers a {@link Map} object
     */
    public Config(final Map<String, Authorizer> authorizers) {
        setAuthorizers(authorizers);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param clients a {@link Clients} object
     * @param authorizers a {@link Map} object
     */
    public Config(final Clients clients, final Map<String, Authorizer> authorizers) {
        this.clients = clients;
        setAuthorizers(authorizers);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param client a {@link Client} object
     * @param authorizers a {@link Map} object
     */
    public Config(final Client client, final Map<String, Authorizer> authorizers) {
        this.clients = new Clients(client);
        setAuthorizers(authorizers);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param authorizers a {@link Map} object
     * @param clients a {@link Client} object
     */
    public Config(final Map<String, Authorizer> authorizers, final Client... clients) {
        this.clients = new Clients(clients);
        setAuthorizers(authorizers);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param callbackUrl a {@link String} object
     * @param authorizers a {@link Map} object
     * @param clients a {@link Client} object
     */
    public Config(final String callbackUrl, final Map<String, Authorizer> authorizers, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
        setAuthorizers(authorizers);
    }

    /**
     * <p>Constructor for Config.</p>
     *
     * @param callbackUrl a {@link String} object
     * @param client a {@link Client} object
     * @param authorizers a {@link Map} object
     */
    public Config(final String callbackUrl, final Client client, final Map<String, Authorizer> authorizers) {
        this.clients = new Clients(callbackUrl, client);
        setAuthorizers(authorizers);
    }

    /**
     * <p>Setter for the field <code>clients</code>.</p>
     *
     * @param clients a {@link Clients} object
     * @return a {@link Config} object
     */
    public Config setClients(final Clients clients) {
        this.clients = clients;
        return this;
    }

    /**
     * <p>addClient.</p>
     *
     * @param client a {@link Client} object
     * @return a {@link Config} object
     */
    public Config addClient(final Client client) {
        this.clients.addClient(client);
        return this;
    }

    /**
     * <p>setAuthorizer.</p>
     *
     * @param authorizer a {@link Authorizer} object
     * @return a {@link Config} object
     */
    public Config setAuthorizer(final Authorizer authorizer) {
        CommonHelper.assertNotNull("authorizer", authorizer);
        this.authorizers.put(authorizer.getClass().getSimpleName(), authorizer);
        return this;
    }

    /**
     * <p>Setter for the field <code>authorizers</code>.</p>
     *
     * @param authorizers a {@link Map} object
     * @return a {@link Config} object
     */
    public Config setAuthorizers(final Map<String, Authorizer> authorizers) {
        CommonHelper.assertNotNull("authorizers", authorizers);
        this.authorizers = authorizers;
        return this;
    }

    /**
     * <p>addAuthorizer.</p>
     *
     * @param name a {@link String} object
     * @param authorizer a {@link Authorizer} object
     * @return a {@link Config} object
     */
    public Config addAuthorizer(final String name, final Authorizer authorizer) {
        authorizers.put(name, authorizer);
        return this;
    }

    /**
     * <p>setMatcher.</p>
     *
     * @param matcher a {@link Matcher} object
     * @return a {@link Config} object
     */
    public Config setMatcher(final Matcher matcher) {
        CommonHelper.assertNotNull("matcher", matcher);
        this.matchers.put(matcher.getClass().getSimpleName(), matcher);
        return this;
    }

    /**
     * <p>Setter for the field <code>matchers</code>.</p>
     *
     * @param matchers a {@link Map} object
     * @return a {@link Config} object
     */
    public Config setMatchers(final Map<String, Matcher> matchers) {
        CommonHelper.assertNotNull("matchers", matchers);
        this.matchers = matchers;
        return this;
    }

    /**
     * <p>addMatcher.</p>
     *
     * @param name a {@link String} object
     * @param matcher a {@link Matcher} object
     * @return a {@link Config} object
     */
    public Config addMatcher(final String name, final Matcher matcher) {
        matchers.put(name, matcher);
        return this;
    }

    /**
     * <p>Setter for the field <code>securityLogic</code>.</p>
     *
     * @param securityLogic a {@link SecurityLogic} object
     * @return a {@link Config} object
     */
    public Config setSecurityLogic(final SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
        return this;
    }

    /**
     * <p>Setter for the field <code>callbackLogic</code>.</p>
     *
     * @param callbackLogic a {@link CallbackLogic} object
     * @return a {@link Config} object
     */
    public Config setCallbackLogic(final CallbackLogic callbackLogic) {
        this.callbackLogic = callbackLogic;
        return this;
    }

    /**
     * <p>Setter for the field <code>logoutLogic</code>.</p>
     *
     * @param logoutLogic a {@link LogoutLogic} object
     * @return a {@link Config} object
     */
    public Config setLogoutLogic(final LogoutLogic logoutLogic) {
        this.logoutLogic = logoutLogic;
        return this;
    }

    /**
     * <p>Setter for the field <code>webContextFactory</code>.</p>
     *
     * @param webContextFactory a {@link WebContextFactory} object
     * @return a {@link Config} object
     */
    public Config setWebContextFactory(final WebContextFactory webContextFactory) {
        this.webContextFactory = webContextFactory;
        return this;
    }

    /**
     * <p>Setter for the field <code>sessionStoreFactory</code>.</p>
     *
     * @param sessionStoreFactory a {@link SessionStoreFactory} object
     * @return a {@link Config} object
     */
    public Config setSessionStoreFactory(final SessionStoreFactory sessionStoreFactory) {
        this.sessionStoreFactory = sessionStoreFactory;
        return this;
    }

    /**
     * <p>Setter for the field <code>profileManagerFactory</code>.</p>
     *
     * @param profileManagerFactory a {@link ProfileManagerFactory} object
     * @return a {@link Config} object
     */
    public Config setProfileManagerFactory(final ProfileManagerFactory profileManagerFactory) {
        this.profileManagerFactory = profileManagerFactory;
        return this;
    }

    /**
     * <p>Setter for the field <code>httpActionAdapter</code>.</p>
     *
     * @param httpActionAdapter a {@link HttpActionAdapter} object
     * @return a {@link Config} object
     */
    public Config setHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
        return this;
    }

    /**
     * <p>setSecurityLogicIfUndefined.</p>
     *
     * @param securityLogic a {@link SecurityLogic} object
     */
    public void setSecurityLogicIfUndefined(final SecurityLogic securityLogic) {
        if (this.securityLogic == null) {
            setSecurityLogic(securityLogic);
        }
    }

    /**
     * <p>setCallbackLogicIfUndefined.</p>
     *
     * @param callbackLogic a {@link CallbackLogic} object
     */
    public void setCallbackLogicIfUndefined(final CallbackLogic callbackLogic) {
        if (this.callbackLogic == null) {
            setCallbackLogic(callbackLogic);
        }
    }

    /**
     * <p>setLogoutLogicIfUndefined.</p>
     *
     * @param logoutLogic a {@link LogoutLogic} object
     */
    public void setLogoutLogicIfUndefined(final LogoutLogic logoutLogic) {
        if (this.logoutLogic == null) {
            setLogoutLogic(logoutLogic);
        }
    }

    /**
     * <p>setWebContextFactoryIfUndefined.</p>
     *
     * @param webContextFactory a {@link WebContextFactory} object
     */
    public void setWebContextFactoryIfUndefined(final WebContextFactory webContextFactory) {
        if (this.webContextFactory == null) {
            setWebContextFactory(webContextFactory);
        }
    }

    /**
     * <p>setSessionStoreFactoryIfUndefined.</p>
     *
     * @param sessionStoreFactory a {@link SessionStoreFactory} object
     */
    public void setSessionStoreFactoryIfUndefined(final SessionStoreFactory sessionStoreFactory) {
        if (this.sessionStoreFactory == null) {
            setSessionStoreFactory(sessionStoreFactory);
        }
    }

    /**
     * <p>setProfileManagerFactoryIfUndefined.</p>
     *
     * @param profileManagerFactory a {@link ProfileManagerFactory} object
     */
    public void setProfileManagerFactoryIfUndefined(final ProfileManagerFactory profileManagerFactory) {
        if (this.profileManagerFactory == null) {
            setProfileManagerFactory(profileManagerFactory);
        }
    }

    /**
     * <p>setHttpActionAdapterIfUndefined.</p>
     *
     * @param httpActionAdapter a {@link HttpActionAdapter} object
     */
    public void setHttpActionAdapterIfUndefined(final HttpActionAdapter httpActionAdapter) {
        if (this.httpActionAdapter == null) {
            setHttpActionAdapter(httpActionAdapter);
        }
    }
}
