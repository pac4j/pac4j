package org.pac4j.core.client.finder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Find the right clients based on the query parameter for the {@link org.pac4j.core.engine.SecurityLogic}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@Slf4j
public class DefaultSecurityClientFinder implements ClientFinder {

    private String clientNameParameter = Pac4jConstants.DEFAULT_FORCE_CLIENT_PARAMETER;

    /** {@inheritDoc} */
    @Override
    public List<Client> find(final Clients clients, final WebContext context, final String clientNames) {
        final List<Client> result = new ArrayList<>();

        var securityClientNames = clientNames;
        // we don't have defined clients to secure the URL, use the general default security ones from the Clients if they exist
        // we check the nullity and not the blankness to allow the blank string to mean no client
        // so no clients parameter -> use the default security ones; clients=blank string -> no clients defined
        LOGGER.debug("Provided clientNames: {}", securityClientNames);
        if (securityClientNames == null) {
            securityClientNames = clients.getDefaultSecurityClients();
            LOGGER.debug("Default security clients: {}", securityClientNames);
            // still no clients defined and we only have one client, use it
            if (securityClientNames == null && clients.findAllClients().size() == 1) {
                securityClientNames = clients.getClients().get(0).getName();
                LOGGER.debug("Only client: {}", securityClientNames);
            }
        }

        if (CommonHelper.isNotBlank(securityClientNames)) {
            val names = Arrays.asList(securityClientNames.split(Pac4jConstants.ELEMENT_SEPARATOR));
            val clientOnRequest = context.getRequestParameter(clientNameParameter);

            // if a client is provided on the request, get the client
            // and check if it is allowed (defined in the list of the clients)
            LOGGER.debug("clientNameOnRequest: {}", clientOnRequest);
            if (clientOnRequest.isPresent()) {
                // from the request
                val client = clients.findClient(clientOnRequest.get());
                if (client.isPresent()) {
                    val nameFound = client.get().getName();
                    // if allowed -> return it
                    for (val name : names) {
                        if (CommonHelper.areEqualsIgnoreCaseAndTrim(name, nameFound)) {
                            result.add(client.get());
                            break;
                        }
                    }
                }
            } else {
                // no client provided, return all
                for (val name : names) {
                    // from its name
                    val client = clients.findClient(name);
                    if (client.isPresent()) {
                        result.add(client.get());
                    }
                }
            }
        }
        LOGGER.debug("result: {}", result.stream().map(Client::getName).collect(Collectors.toList()));
        return result;
    }
}
