package org.pac4j.core.client.finder;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Find the right client on the callback.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class DefaultCallbackClientFinder implements ClientFinder {

    private IndirectClient defaultClient;

    public DefaultCallbackClientFinder() {}

    public DefaultCallbackClientFinder(final IndirectClient defaultClient) {
        this.defaultClient = defaultClient;
    }

    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {

        final List<Client> result = new ArrayList<>();

        for (final Client client : clients.findAllClients()) {
            if (client instanceof IndirectClient) {
                final IndirectClient indirectClient = (IndirectClient) client;
                indirectClient.init();
                if (indirectClient.getCallbackUrlResolver().matches(indirectClient.getName(), context)) {
                    result.add(indirectClient);
                }
            }
        }

        if (result.size() == 0 && defaultClient != null) {
            return Arrays.asList(defaultClient);
        } else {
            return result;
        }
    }

    public IndirectClient getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(final IndirectClient defaultClient) {
        this.defaultClient = defaultClient;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "defaultClient", defaultClient);
    }
}
