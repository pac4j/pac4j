package org.pac4j.saml.store;

import com.hazelcast.core.HazelcastInstance;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Default store factory which provides HTTP Session store.
 *
 * @author Francesco Chicchiriccò
 * @since 5.0.1
 */
public class HazelcastSAMLMessageStoreFactory implements SAMLMessageStoreFactory {

    private final HazelcastInstance hazelcastInstance;

    /**
     * <p>Constructor for HazelcastSAMLMessageStoreFactory.</p>
     *
     * @param hazelcastInstance a {@link HazelcastInstance} object
     */
    public HazelcastSAMLMessageStoreFactory(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    /** {@inheritDoc} */
    @Override
    public SAMLMessageStore getMessageStore(final WebContext context, final SessionStore sessionStore) {
        return new HazelcastSAMLMessageStore(hazelcastInstance);
    }
}
