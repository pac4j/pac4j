package org.pac4j.config.builder;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Client;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for CAS clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasClientBuilder extends AbstractBuilder {

    public CasClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateCasClient(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final var loginUrl = getProperty(CAS_LOGIN_URL, i);
            final var protocol = getProperty(CAS_PROTOCOL, i);
            if (isNotBlank(loginUrl)) {
                var configuration = new CasConfiguration();
                final var casClient = new CasClient(configuration);
                configuration.setLoginUrl(loginUrl);
                if (isNotBlank(protocol)) {
                    configuration.setProtocol(CasProtocol.valueOf(protocol));
                }
                casClient.setName(concat(casClient.getName(), i));
                clients.add(casClient);
            }
        }
    }
}
