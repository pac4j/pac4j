package org.pac4j.core.client.finder;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Find the right client on the callback.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class DefaultCallbackClientFinder implements ClientFinder {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCallbackClientFinder.class);

    public DefaultCallbackClientFinder() {}

    @Override
    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {

        final List<Client> result = new ArrayList<>();
        final List<Client> indirectClients = new ArrayList<>();

        for (final var client : clients.findAllClients()) {
            if (client instanceof IndirectClient) {
                final var indirectClient = (IndirectClient) client;
                indirectClients.add(client);
                indirectClient.init();
                if (indirectClient.getCallbackUrlResolver().matches(indirectClient.getName(), context)) {
                    result.add(indirectClient);
                }
            }
        }
        logger.debug("result: {}", result.stream().map(Client::getName).collect(Collectors.toList()));

        // fallback: no client found and we have a default client, use it
        if (result.isEmpty() && CommonHelper.isNotBlank(clientNames)) {
            final var defaultClient = clients.findClient(clientNames);
            if (defaultClient.isPresent()) {
                logger.debug("Defaulting to the configured client: {}", defaultClient);
                result.add(defaultClient.get());
            }
        }
        // fallback: no client found and we only have one indirect client, use it
        if (result.isEmpty() && indirectClients.size() == 1) {
            logger.debug("Defaulting to the only client: {}", indirectClients.get(0));
            result.addAll(indirectClients);
        }

        return result;
    }
}
