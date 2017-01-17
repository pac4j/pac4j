package org.pac4j.oauth.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.credentials.authenticator.OAuth10Authenticator;
import org.pac4j.oauth.credentials.extractor.OAuth10CredentialsExtractor;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.creator.OAuth10ProfileCreator;
import org.pac4j.oauth.redirect.OAuth10RedirectActionBuilder;

/**
 * The generic OAuth 1.0 client.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10Client<U extends OAuth10Profile> extends IndirectClient<OAuth10Credentials, U> {

    protected OAuth10Configuration configuration = new OAuth10Configuration();

    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);

        setRedirectActionBuilder(new OAuth10RedirectActionBuilder(configuration));
        setCredentialsExtractor(new OAuth10CredentialsExtractor(configuration));
        setAuthenticator(new OAuth10Authenticator(configuration));
        setProfileCreator(new OAuth10ProfileCreator<>(configuration));
    }

    public OAuth10Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OAuth10Configuration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.configuration.setClient(this);
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
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", getCallbackUrl(),
                "callbackUrlResolver", getCallbackUrlResolver(), "ajaxRequestResolver", getAjaxRequestResolver(),
                "redirectActionBuilder", getRedirectActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(), "configuration", this.configuration);
    }
}
