package org.pac4j.config.builder;

import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.direct.DirectBasicAuthClient;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for direct clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class DirectClientBuilder extends AbstractBuilder {

    public DirectClientBuilder(final Map<String, String> properties, final Map<String, Authenticator> authenticators) {
        super(properties, authenticators);
    }

    public void tryCreateAnonymousClient(final List<Client> clients) {
        val anonymous = getProperty(ANONYMOUS);
        if (isNotBlank(anonymous)) {
            clients.add(new AnonymousClient());
        }
    }

    public void tryCreateDirectBasciAuthClient(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val authenticator = getProperty(DIRECTBASICAUTH_AUTHENTICATOR, i);
            if (isNotBlank(authenticator)) {
                val directBasicAuthClient = new DirectBasicAuthClient();
                directBasicAuthClient.setAuthenticator(getAuthenticator(authenticator));
                directBasicAuthClient.setName(concat(directBasicAuthClient.getName(), i));
                clients.add(directBasicAuthClient);
            }
        }
    }
}
