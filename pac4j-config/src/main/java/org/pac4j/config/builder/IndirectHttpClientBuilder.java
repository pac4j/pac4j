package org.pac4j.config.builder;

import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;

import java.util.Collection;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for indirect HTTP clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class IndirectHttpClientBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for IndirectHttpClientBuilder.</p>
     *
     * @param properties a {@link Map} object
     * @param authenticators a {@link Map} object
     */
    public IndirectHttpClientBuilder(final Map<String, String> properties, final Map<String, Authenticator> authenticators) {
        super(properties, authenticators);
    }

    /**
     * <p>tryCreateLoginFormClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateLoginFormClient(final Collection<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val loginUrl = getProperty(FORMCLIENT_LOGIN_URL, i);
            val authenticator = getProperty(FORMCLIENT_AUTHENTICATOR, i);
            if (isNotBlank(loginUrl) && isNotBlank(authenticator)) {
                val formClient = new FormClient();
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

    /**
     * <p>tryCreateIndirectBasicAuthClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateIndirectBasicAuthClient(final Collection<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val authenticator = getProperty(INDIRECTBASICAUTH_AUTHENTICATOR, i);
            if (isNotBlank(authenticator)) {
                val indirectBasicAuthClient = new IndirectBasicAuthClient();
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
