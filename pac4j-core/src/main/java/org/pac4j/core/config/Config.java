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

    private ProfileManagerFactory profileManagerFactory = ProfileManagerFactory.DEFAULT;

    protected Clients clients = new Clients();

    protected Map<String, Authorizer> authorizers = new HashMap<>();

    protected Map<String, Matcher> matchers = new HashMap<>();

    protected HttpActionAdapter httpActionAdapter;

    protected SecurityLogic securityLogic = DefaultSecurityLogic.INSTANCE;

    protected CallbackLogic callbackLogic = DefaultCallbackLogic.INSTANCE;

    protected LogoutLogic logoutLogic = DefaultLogoutLogic.INSTANCE;

    protected WebContextFactory webContextFactory;

    protected SessionStoreFactory sessionStoreFactory;

    public Config() {}

    public Config(final Client client) {
        this.clients = new Clients(client);
    }

    public Config(final Clients clients) {
        this.clients = clients;
    }

    public Config(final List<Client> clients) {
        this.clients = new Clients(clients);
    }

    public Config(final Client... clients) {
        this.clients = new Clients(clients);
    }

    public Config(final String callbackUrl, final Client client) {
        this.clients = new Clients(callbackUrl, client);
    }

    public Config(final String callbackUrl, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    public Config(final String callbackUrl, final List<Client> clients) {
        this.clients = new Clients(callbackUrl, clients);
    }

    public Config(final Map<String, Authorizer> authorizers) {
        setAuthorizers(authorizers);
    }

    public Config(final Clients clients, final Map<String, Authorizer> authorizers) {
        this.clients = clients;
        setAuthorizers(authorizers);
    }

    public Config(final Client client, final Map<String, Authorizer> authorizers) {
        this.clients = new Clients(client);
        setAuthorizers(authorizers);
    }

    public Config(final Map<String, Authorizer> authorizers, final Client... clients) {
        this.clients = new Clients(clients);
        setAuthorizers(authorizers);
    }

    public Config(final String callbackUrl, final Map<String, Authorizer> authorizers, final Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
        setAuthorizers(authorizers);
    }

    public Config(final String callbackUrl, final Client client, final Map<String, Authorizer> authorizers) {
        this.clients = new Clients(callbackUrl, client);
        setAuthorizers(authorizers);
    }

    public Config setAuthorizer(final Authorizer authorizer) {
        CommonHelper.assertNotNull("authorizer", authorizer);
        this.authorizers.put(authorizer.getClass().getSimpleName(), authorizer);
        return this;
    }

    public Config setAuthorizers(final Map<String, Authorizer> authorizers) {
        CommonHelper.assertNotNull("authorizers", authorizers);
        this.authorizers = authorizers;
        return this;
    }

    public Config addAuthorizer(final String name, final Authorizer authorizer) {
        authorizers.put(name, authorizer);
        return this;
    }

    public Config setMatcher(final Matcher matcher) {
        CommonHelper.assertNotNull("matcher", matcher);
        this.matchers.put(matcher.getClass().getSimpleName(), matcher);
        return this;
    }

    public Config setMatchers(final Map<String, Matcher> matchers) {
        CommonHelper.assertNotNull("matchers", matchers);
        this.matchers = matchers;
        return this;
    }

    public Config addMatcher(final String name, final Matcher matcher) {
        matchers.put(name, matcher);
        return this;
    }

    public Config setProfileManagerFactory(final ProfileManagerFactory profileManagerFactory) {
        this.profileManagerFactory = profileManagerFactory;
        return this;
    }

    public Config setClients(final Clients clients) {
        this.clients = clients;
        return this;
    }

    public Config addClient(final Client client) {
        this.clients.addClient(client);
        return this;
    }

    public Config setHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
        return this;
    }

    public Config setSecurityLogic(final SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
        return this;
    }

    public Config setCallbackLogic(final CallbackLogic callbackLogic) {
        this.callbackLogic = callbackLogic;
        return this;
    }

    public Config setLogoutLogic(final LogoutLogic logoutLogic) {
        this.logoutLogic = logoutLogic;
        return this;
    }

    public Config setWebContextFactory(final WebContextFactory webContextFactory) {
        this.webContextFactory = webContextFactory;
        return this;
    }

    public Config setSessionStoreFactory(final SessionStoreFactory sessionStoreFactory) {
        this.sessionStoreFactory = sessionStoreFactory;
        return this;
    }

    public void setSessionStoreFactoryIfUndefined(final SessionStoreFactory sessionStoreFactory) {
        if (this.sessionStoreFactory == null) {
            this.sessionStoreFactory = sessionStoreFactory;
        }
    }

    public void setWebContextFactoryIfUndefined(final WebContextFactory webContextFactory) {
        if (this.webContextFactory == null) {
            setWebContextFactory(webContextFactory);
        }
    }

    public void setHttpActionAdapterIfUndefined(final HttpActionAdapter httpActionAdapter) {
        if (this.httpActionAdapter == null) {
            setHttpActionAdapter(httpActionAdapter);
        }

    }
}
