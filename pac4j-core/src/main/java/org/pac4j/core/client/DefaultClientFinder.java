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
package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Find the right client based on regular parameters.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultClientFinder {

    public static final String CLIENT_NAME_SEPARATOR = ",";

    public Client find(final Clients clients, final WebContext context, final String clientName) {
        // no name -> no client
        if (CommonHelper.isBlank(clientName)) {
            return null;
        }
        final List<String> names = Arrays.asList(clientName.split(CLIENT_NAME_SEPARATOR));
        // if a client_name parameter is provided on the request, get the client and check if it is allowed
        final String clientNameOnRequest = context.getRequestParameter(clients.getClientNameParameter());
        if (clientNameOnRequest != null) {
            // from the request
            final Client client = clients.findClient(context);
            final String nameFound = client.getName();
            // if allowed
            if (names.contains(nameFound)) {
                return client;
            }
        }
        // not allowed, return default (first)
        return clients.findClient(names.get(0));
    }
}
