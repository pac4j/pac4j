package org.pac4j.core.client.finder;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Find the right clients based on the query parameter for the {@link org.pac4j.core.engine.SecurityLogic}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultSecurityClientFinder implements ClientFinder {

    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {
        final List<Client> result = new ArrayList<>();

        if (CommonHelper.isNotBlank(clientNames)) {
            final List<String> names = Arrays.asList(clientNames.split(Pac4jConstants.ELEMENT_SEPRATOR));
            // if a "client_name" parameter is provided on the request, get the client
            // and check if it is allowed (defined in the list of the clients)
            final String clientNameOnRequest = context.getRequestParameter(clientNameParameter);
            if (clientNameOnRequest != null) {
                // from the request
                final Client client = clients.findClient(clientNameOnRequest);
                final String nameFound = client.getName();
                // if allowed -> return it
                boolean found = false;
                for (final String name : names) {
                    if (CommonHelper.areEqualsIgnoreCaseAndTrim(name, nameFound)) {
                        result.add(client);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new TechnicalException("Client not allowed: " + nameFound);
                }
            } else {
                // no client provided, return all
                for (final String name : names) {
                    // from its name
                    final Client client = clients.findClient(name);
                    result.add(client);
                }
            }
        }
        return result;
    }

    public String getClientNameParameter() {
        return clientNameParameter;
    }

    public void setClientNameParameter(final String clientNameParameter) {
        this.clientNameParameter = clientNameParameter;
    }
}
