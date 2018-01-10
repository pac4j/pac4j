package org.pac4j.core.client.finder;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Find the right client on the callback.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class DefaultCallbackClientFinder implements ClientFinder {

    public DefaultCallbackClientFinder() {}

    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {

        final List<Client> result = new ArrayList<>();
        final List<Client> indirectClients = new ArrayList<>();

        for (final Client client : clients.findAllClients()) {
            if (client instanceof IndirectClient) {
                final IndirectClient indirectClient = (IndirectClient) client;
                indirectClients.add(client);
                indirectClient.init();
                if (indirectClient.getCallbackUrlResolver().matches(indirectClient.getName(), context)) {
                    result.add(indirectClient);
                }
            }
        }

        // fallback: we didn't find any client on the URL
        if (result.isEmpty()) {
            //  we have a default client, use it
            if (CommonHelper.isNotBlank(clientNames)) {
                result.add(clients.findClient(clientNames));
                // or we only have one indirect client, use it
            } else if (indirectClients.size() == 1){
                result.addAll(indirectClients);
            }
        }

        return result;
    }
}
