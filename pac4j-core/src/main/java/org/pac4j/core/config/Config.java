package org.pac4j.core.config;

import lombok.Getter;
import lombok.Setter;
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
public class Config implements Cloneable {

    private ProfileManagerFactory profileManagerFactory = ProfileManagerFactory.DEFAULT;

    protected Clients clients = new Clients();

    protected Map<String, Authorizer> authorizers = new HashMap<>();

    protected Map<String, Matcher> matchers = new HashMap<>();

    protected HttpActionAdapter httpActionAdapter;

    protected SecurityLogic securityLogic = new DefaultSecurityLogic();

    protected CallbackLogic callbackLogic = new DefaultCallbackLogic();

    protected LogoutLogic logoutLogic = new DefaultLogoutLogic();

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

    public void setAuthorizer(final Authorizer authorizer) {
        CommonHelper.assertNotNull("authorizer", authorizer);
        this.authorizers.put(authorizer.getClass().getSimpleName(), authorizer);
    }

    public void setAuthorizers(final Map<String, Authorizer> authorizers) {
        CommonHelper.assertNotNull("authorizers", authorizers);
        this.authorizers = authorizers;
    }

    public void addAuthorizer(final String name, final Authorizer authorizer) {
        authorizers.put(name, authorizer);
    }

    public void setMatcher(final Matcher matcher) {
        CommonHelper.assertNotNull("matcher", matcher);
        this.matchers.put(matcher.getClass().getSimpleName(), matcher);
    }

    public void setMatchers(final Map<String, Matcher> matchers) {
        CommonHelper.assertNotNull("matchers", matchers);
        this.matchers = matchers;
    }

    public void addMatcher(final String name, final Matcher matcher) {
        matchers.put(name, matcher);
    }

    public void defaultSessionStoreFactory(final SessionStoreFactory sessionStoreFactory) {
        if (this.sessionStoreFactory == null) {
            this.sessionStoreFactory = sessionStoreFactory;
        }
    }

    public void defaultWebContextFactory(final WebContextFactory webContextFactory) {
        if (this.webContextFactory == null) {
            setWebContextFactory(webContextFactory);
        }
    }

    public void defaultHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        if (this.httpActionAdapter == null) {
            setHttpActionAdapter(httpActionAdapter);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
