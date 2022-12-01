package org.pac4j.oauth.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.authenticator.OAuth20Authenticator;
import org.pac4j.oauth.credentials.extractor.OAuth20CredentialsExtractor;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.redirect.OAuth20RedirectionActionBuilder;

/**
 * The generic OAuth 2.0 client.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString(callSuper = true)
public class OAuth20Client extends IndirectClient {

    @Getter
    @Setter
    protected OAuth20Configuration configuration = new OAuth20Configuration();

    @Override
    protected void beforeInternalInit(final boolean forceReinit) {
        super.beforeInternalInit(forceReinit);
        CommonHelper.assertNotNull("configuration", configuration);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        defaultRedirectionActionBuilder(new OAuth20RedirectionActionBuilder(configuration, this));
        defaultCredentialsExtractor(new OAuth20CredentialsExtractor(configuration, this));
        defaultAuthenticator(new OAuth20Authenticator(configuration, this));
        defaultProfileCreator(new OAuth20ProfileCreator(configuration, this));
    }

    public String getKey() {
        return configuration.getKey();
    }

    public void setKey(final String key) {
        configuration.setKey(key);
    }

    public String getSecret() {
        return configuration.getSecret();
    }

    public void setSecret(final String secret) {
        configuration.setSecret(secret);
    }
}
