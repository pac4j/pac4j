package org.pac4j.config.builder;

import lombok.val;
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

    /**
     * <p>Constructor for CasClientBuilder.</p>
     *
     * @param properties a {@link java.util.Map} object
     */
    public CasClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryCreateCasClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateCasClient(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val loginUrl = getProperty(CAS_LOGIN_URL, i);
            val protocol = getProperty(CAS_PROTOCOL, i);
            if (isNotBlank(loginUrl)) {
                var configuration = new CasConfiguration();
                val casClient = new CasClient(configuration);
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
