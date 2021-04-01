package org.pac4j.saml.store;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.opensaml.core.xml.XMLObject;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implements store of SAML messages and uses Hazelcast as underlying dataStore.
 * As the XMLObjects can't be serialized (which could lead to problems during failover), the messages are transformed
 * into SAMLObject which internally marshalls the content into XML during serialization.
 * Base64 encoding / decoding is also used to reduce space allocation.
 */
public class HazelcastSAMLMessageStore implements SAMLMessageStore {

    private static final Logger LOG = LoggerFactory.getLogger(HazelcastSAMLMessageStore.class);

    private static final String MAP_NAME = HazelcastSAMLMessageStore.class.getSimpleName();

    private final HazelcastInstance hazelcastInstance;

    public HazelcastSAMLMessageStore(final HazelcastInstance hazelcastInstance) {
        CommonHelper.assertNotNull("hazelcastInstance", hazelcastInstance);
        this.hazelcastInstance = hazelcastInstance;
    }

    private IMap<String, String> getStoreMapInstance() {
        IMap<String, String> inst = hazelcastInstance.getMap(MAP_NAME);
        LOG.debug("Located Hazelcast map instance [{}]", MAP_NAME);
        return inst;
    }

    @Override
    public Optional<XMLObject> get(final String messageID) {
        IMap<String, String> map = getStoreMapInstance();
        LOG.debug("Attempting to get message {} from Hazelcast map {}", messageID, MAP_NAME);

        String message = map.get(messageID);
        if (message == null) {
            LOG.debug("Message {} not found in Hazelcast map {}", messageID, MAP_NAME);
            return Optional.empty();
        }

        LOG.debug("Message {} found in Hazelcast map {}, clearing", messageID, MAP_NAME);
        map.remove(messageID);

        return Configuration.deserializeSamlObject(
                new String(Base64.getDecoder().decode(message), StandardCharsets.UTF_8));
    }

    @Override
    public void set(final String messageID, final XMLObject message) {
        IMap<String, String> map = getStoreMapInstance();
        LOG.debug("Storing message {} to Hazelcast map {}", messageID, MAP_NAME);
        map.put(messageID, Base64.getEncoder().encodeToString(
                Configuration.serializeSamlObject(message).toString().getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void remove(final String messageID) {
        IMap<String, String> map = getStoreMapInstance();
        LOG.debug("Removing message {} from Hazelcast map {}", messageID, MAP_NAME);
        map.remove(messageID);
    }
}
