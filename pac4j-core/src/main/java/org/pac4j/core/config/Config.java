/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.core.config;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.matching.Matcher;
import org.pac4j.core.util.CommonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The default configuration with clients, authorizers and matchers.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class Config {

    protected Clients clients;

    protected Map<String, Authorizer> authorizers = new HashMap<>();

    protected Map<String, Matcher> matchers = new HashMap<>();

    protected SessionStore sessionStore;

    protected HttpActionAdapter httpActionAdapter;

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

    public void setSessionStore(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public HttpActionAdapter getHttpActionAdapter() {
        return httpActionAdapter;
    }

    public void setHttpActionAdapter(HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }
}
