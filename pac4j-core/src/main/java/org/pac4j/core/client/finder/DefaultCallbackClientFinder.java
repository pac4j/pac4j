package org.pac4j.core.client.finder;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Find the right client on the callback.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
@Slf4j
public class DefaultCallbackClientFinder implements ClientFinder {

    /**
     * <p>Constructor for DefaultCallbackClientFinder.</p>
     */
    public DefaultCallbackClientFinder() {}

    /** {@inheritDoc} */
    @Override
    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {

        val result = new ArrayList<Client>();
        val indirectClients = new ArrayList<Client>();

        for (val client : clients.findAllClients()) {
            if (client instanceof IndirectClient indirectClient) {
                indirectClients.add(client);
                indirectClient.init();
                if (indirectClient.getCallbackUrlResolver().matches(indirectClient.getName(), context)) {
                    result.add(indirectClient);
                }
            }
        }
        LOGGER.debug("result: {}", result.stream().map(Client::getName).collect(Collectors.toList()));

        // fallback: no client found and we have a default client, use it
        if (result.isEmpty() && CommonHelper.isNotBlank(clientNames)) {
            val defaultClient = clients.findClient(clientNames);
            if (defaultClient.isPresent()) {
                LOGGER.debug("Defaulting to the configured client: {}", defaultClient);
                result.add(defaultClient.get());
            }
        }
        // fallback: no client found and we only have one indirect client, use it
        if (result.isEmpty() && indirectClients.size() == 1) {
            LOGGER.debug("Defaulting to the only client: {}", indirectClients.get(0));
            result.addAll(indirectClients);
        }

        return result;
    }
}
