package org.pac4j.oauth.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.authenticator.OAuth10Authenticator;
import org.pac4j.oauth.credentials.extractor.OAuth10CredentialsExtractor;
import org.pac4j.oauth.profile.creator.OAuth10ProfileCreator;
import org.pac4j.oauth.redirect.OAuth10RedirectionActionBuilder;

/**
 * The generic OAuth 1.0 client.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString(callSuper = true)
public class OAuth10Client extends IndirectClient {

    @Getter
    @Setter
    protected OAuth10Configuration configuration = new OAuth10Configuration();

    /** {@inheritDoc} */
    @Override
    protected void beforeInternalInit(final boolean forceReinit) {
        super.beforeInternalInit(forceReinit);
        CommonHelper.assertNotNull("configuration", configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setRedirectionActionBuilderIfUndefined(new OAuth10RedirectionActionBuilder(configuration, this));
        setCredentialsExtractorIfUndefined(new OAuth10CredentialsExtractor(configuration, this));
        setAuthenticatorIfUndefined(new OAuth10Authenticator(configuration, this));
        setProfileCreatorIfUndefined(new OAuth10ProfileCreator(configuration, this));
    }

    /**
     * <p>getKey.</p>
     *
     * @return a {@link String} object
     */
    public String getKey() {
        return configuration.getKey();
    }

    /**
     * <p>setKey.</p>
     *
     * @param key a {@link String} object
     */
    public void setKey(final String key) {
        configuration.setKey(key);
    }

    /**
     * <p>getSecret.</p>
     *
     * @return a {@link String} object
     */
    public String getSecret() {
        return configuration.getSecret();
    }

    /**
     * <p>setSecret.</p>
     *
     * @param secret a {@link String} object
     */
    public void setSecret(final String secret) {
        configuration.setSecret(secret);
    }
}
