package org.pac4j.config.builder;

import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for indirect HTTP clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class IndirectHttpClientBuilder extends AbstractBuilder implements PropertiesConstants {

    private final Map<String, Authenticator> authenticators;

    public IndirectHttpClientBuilder(final Map<String, String> properties, final Map<String, Authenticator> authenticators) {
        super(properties);
        this.authenticators = authenticators;
    }

    public void tryCreateLoginFormClient(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String loginUrl = getProperty(FORMCLIENT_LOGIN_URL, i);
            final String authenticator = getProperty(FORMCLIENT_AUTHENTICATOR, i);
            if (isNotBlank(loginUrl) && isNotBlank(authenticator)) {
                final FormClient formClient = new FormClient();
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

    public void tryCreateIndirectBasciAuthClient(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String authenticator = getProperty(INDIRECTBASICAUTH_AUTHENTICATOR, i);
            if (isNotBlank(authenticator)) {
                final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient();
                indirectBasicAuthClient.setAuthenticator(getAuthenticator(authenticator));
                if (containsProperty(INDIRECTBASICAUTH_REALM_NAME, i)) {
                    indirectBasicAuthClient.setRealmName(getProperty(INDIRECTBASICAUTH_REALM_NAME, i));
                }
                indirectBasicAuthClient.setName(concat(indirectBasicAuthClient.getName(), i));
                clients.add(indirectBasicAuthClient);
            }
        }
    }

    protected Authenticator getAuthenticator(final String name) {
        if (AUTHENTICATOR_TEST_TOKEN.equals(name)) {
            return new SimpleTestTokenAuthenticator();
        } else if (AUTHENTICATOR_TEST_USERNAME_PASSWORD.equals(name)) {
            return new SimpleTestUsernamePasswordAuthenticator();
        } else {
            return authenticators.get(name);
        }
    }
}
