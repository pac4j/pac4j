package org.pac4j.oauth.client;

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
public class OAuth20Client extends IndirectClient {

    protected OAuth20Configuration configuration = new OAuth20Configuration();

    @Override
    protected void beforeInternalInit() {
        super.beforeInternalInit();
        CommonHelper.assertNotNull("configuration", configuration);
    }

    @Override
    protected void internalInit() {
        defaultRedirectionActionBuilder(new OAuth20RedirectionActionBuilder(configuration, this));
        defaultCredentialsExtractor(new OAuth20CredentialsExtractor(configuration, this));
        defaultAuthenticator(new OAuth20Authenticator(configuration, this));
        defaultProfileCreator(new OAuth20ProfileCreator(configuration, this));
    }

    public OAuth20Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OAuth20Configuration configuration) {
        this.configuration = configuration;
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

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "callbackUrl", getCallbackUrl(),
                "callbackUrlResolver", getCallbackUrlResolver(), "ajaxRequestResolver", getAjaxRequestResolver(),
                "redirectionActionBuilder", getRedirectionActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(), "configuration", this.configuration);
    }
}
