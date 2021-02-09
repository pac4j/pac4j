package org.pac4j.config.builder;

import org.pac4j.core.client.Client;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for indirect HTTP clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class IndirectHttpClientBuilder extends AbstractBuilder {

    public IndirectHttpClientBuilder(final Map<String, String> properties, final Map<String, Authenticator> authenticators) {
        super(properties, authenticators);
    }

    public void tryCreateLoginFormClient(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final var loginUrl = getProperty(FORMCLIENT_LOGIN_URL, i);
            final var authenticator = getProperty(FORMCLIENT_AUTHENTICATOR, i);
            if (isNotBlank(loginUrl) && isNotBlank(authenticator)) {
                final var formClient = new FormClient();
                formClient.setLoginUrl(loginUrl);
                formClient.setAuthenticator(getAuthenticator(authenticator));
                if (containsProperty(FORMCLIENT_USERNAME_PARAMETER, i)) {
                    formClient.setUsernameParameter(getProperty(FORMCLIENT_USERNAME_PARAMETER, i));
                }
                if (containsProperty(FORMCLIENT_PASSWORD_PARAMETER, i)) {
                    formClient.setPasswordParameter(getProperty(FORMCLIENT_PASSWORD_PARAMETER, i));
                }
                formClient.setName(concat(formClient.getName(), i));
                clients.add(formClient);
            }
        }
    }

    public void tryCreateIndirectBasicAuthClient(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final var authenticator = getProperty(INDIRECTBASICAUTH_AUTHENTICATOR, i);
            if (isNotBlank(authenticator)) {
                final var indirectBasicAuthClient = new IndirectBasicAuthClient();
                indirectBasicAuthClient.setAuthenticator(getAuthenticator(authenticator));
                if (containsProperty(INDIRECTBASICAUTH_REALM_NAME, i)) {
                    indirectBasicAuthClient.setRealmName(getProperty(INDIRECTBASICAUTH_REALM_NAME, i));
                }
                indirectBasicAuthClient.setName(concat(indirectBasicAuthClient.getName(), i));
                clients.add(indirectBasicAuthClient);
            }
        }
    }
}
