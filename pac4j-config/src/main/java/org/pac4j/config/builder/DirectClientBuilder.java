package org.pac4j.config.builder;

import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.direct.AnonymousClient;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for direct clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class DirectClientBuilder extends AbstractBuilder implements PropertiesConstants {

    public DirectClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateAnonymousClient(final List<Client> clients) {
        final String anonymous = getProperty(ANONYMOUS);
        if (isNotBlank(anonymous)) {
            clients.add(new AnonymousClient());
        }
    }
}
