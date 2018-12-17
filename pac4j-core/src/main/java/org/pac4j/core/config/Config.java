package org.pac4j.core.config;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The default configuration with clients, authorizers, matchers, etc.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static Function<WebContext, ProfileManager> profileManagerFactory;
    private static BiFunction<WebContext, SessionStore, ProfileManager> profileManagerFactory2;

    protected Clients clients;

    protected Map<String, Authorizer> authorizers = new HashMap<>();

    protected Map<String, Matcher> matchers = new HashMap<>();

    protected SessionStore sessionStore;

    protected HttpActionAdapter httpActionAdapter;

    protected SecurityLogic securityLogic;

    protected CallbackLogic callbackLogic;

    protected LogoutLogic logoutLogic;

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

    public Clients getClients() {
        return clients;
    }

    public void setClients(final Clients clients) {
        this.clients = clients;
    }

    public Map<String, Authorizer> getAuthorizers() {
        return authorizers;
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

    public Map<String, Matcher> getMatchers() {
        return matchers;
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

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public void setSessionStore(final SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public HttpActionAdapter getHttpActionAdapter() {
        return httpActionAdapter;
    }

    public void setHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }

    public SecurityLogic getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(final SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
    }

    public CallbackLogic getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public LogoutLogic getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(final LogoutLogic logoutLogic) {
        this.logoutLogic = logoutLogic;
    }

    public static Function<WebContext, ProfileManager> getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public static void setProfileManagerFactory(final String name, final Function<WebContext, ProfileManager> profileManagerFactory) {
        CommonHelper.assertNotNull("profileManagerFactory", profileManagerFactory);
        LOGGER.info("Setting Config.profileManagerFactory: {}", name);
        Config.profileManagerFactory = profileManagerFactory;
    }

    public static void defaultProfileManagerFactory(final String name, final Function<WebContext, ProfileManager> profileManagerFactory) {
        if (Config.profileManagerFactory == null) {
            setProfileManagerFactory(name, profileManagerFactory);
        }
    }

    public static BiFunction<WebContext, SessionStore, ProfileManager> getProfileManagerFactory2() {
        return profileManagerFactory2;
    }

    public static void setProfileManagerFactory2(final String name,
                                                 final BiFunction<WebContext, SessionStore, ProfileManager> profileManagerFactory2) {
        CommonHelper.assertNotNull("profileManagerFactory2", profileManagerFactory2);
        LOGGER.info("Setting Config.profileManagerFactory2: {}", name);
        Config.profileManagerFactory2 = profileManagerFactory2;
    }

    public static void defaultProfileManagerFactory2(final String name,
                                                     final BiFunction<WebContext, SessionStore, ProfileManager> profileManagerFactory2) {
        if (Config.profileManagerFactory2 == null) {
            setProfileManagerFactory2(name, profileManagerFactory2);
        }
    }
}
