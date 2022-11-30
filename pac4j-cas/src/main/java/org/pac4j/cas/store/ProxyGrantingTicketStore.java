package org.pac4j.cas.store;

import lombok.Getter;
import lombok.ToString;
import org.apereo.cas.client.proxy.ProxyGrantingTicketStorage;
import org.pac4j.core.store.Store;

/**
 * Apereo CAS client {@link ProxyGrantingTicketStorage} wrapper of a pac4j {@link Store}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString
public class ProxyGrantingTicketStore implements ProxyGrantingTicketStorage {

    @Getter
    private final Store<String, String> store;

    public ProxyGrantingTicketStore(final Store<String, String> store) {
        this.store = store;
    }

    @Override
    public void save(final String proxyGrantingTicketIou, final String proxyGrantingTicket) {
        store.set(proxyGrantingTicketIou, proxyGrantingTicket);
    }

    @Override
    public String retrieve(final String proxyGrantingTicketIou) {
        return store.get(proxyGrantingTicketIou).orElse(null);
    }

    @Override
    public void cleanUp() {
        // never called
    }
}
