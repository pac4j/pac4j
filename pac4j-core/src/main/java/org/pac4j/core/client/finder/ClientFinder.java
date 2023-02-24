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
     * @param clients a {@link org.pac4j.core.client.Clients} object
     * @param context a {@link org.pac4j.core.context.WebContext} object
     * @param clientNames a {@link java.lang.String} object
     * @return a {@link java.util.List} object
     */
    List<Client> find(Clients clients, WebContext context, String clientNames);
}
