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
import org.pac4j.core.client.Clients;

import java.util.HashMap;
import java.util.Map;

/**
 * A basic configuration with clients and authorizers.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class Config {

    protected Clients clients;

    protected Map<String, Authorizer> authorizers = new HashMap<>();

    public Config() {}

    public Config(final Clients clients) {
        this.clients = clients;
    }

    public Config(final Map<String, Authorizer> authorizers) {
        this.authorizers = authorizers;
    }

    public Config(final Clients clients, final Map<String, Authorizer> authorizers) {
        this.clients = clients;
        this.authorizers = authorizers;
    }

    public Clients getClients() {
        return clients;
    }

    public void setClients(Clients clients) {
        this.clients = clients;
    }

    public Map<String, Authorizer> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(Map<String, Authorizer> authorizers) {
        this.authorizers = authorizers;
    }
}
