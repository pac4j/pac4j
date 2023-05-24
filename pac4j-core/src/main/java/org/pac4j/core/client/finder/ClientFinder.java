package org.pac4j.core.client.finder;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContext;

import java.util.List;

/**
 * The way to find the client.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@FunctionalInterface
public interface ClientFinder {

    /**
     * <p>find.</p>
     *
     * @param clients a {@link Clients} object
     * @param context a {@link WebContext} object
     * @param clientNames a {@link String} object
     * @return a {@link List} object
     */
    List<Client> find(Clients clients, WebContext context, String clientNames);
}
