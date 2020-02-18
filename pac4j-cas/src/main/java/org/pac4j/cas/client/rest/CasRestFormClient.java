package org.pac4j.cas.client.rest;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.util.CommonHelper;

/**
 * Direct client which receives credentials as form parameters and validates them via the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestFormClient extends AbstractCasRestClient {

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public CasRestFormClient() {}

    public CasRestFormClient(final CasConfiguration configuration, final String usernameParameter, final String passwordParameter) {
        this.configuration = configuration;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotBlank("usernameParameter", this.usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", this.passwordParameter);
        CommonHelper.assertNotNull("configuration", this.configuration);

        defaultCredentialsExtractor(new FormExtractor(this.usernameParameter, this.passwordParameter));
        defaultAuthenticator(new CasRestAuthenticator(this.configuration));
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public void setPasswordParameter(final String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "authorizationGenerators", getAuthorizationGenerators(), "configuration", this.configuration,
            "usernameParameter", this.usernameParameter, "passwordParameter", this.passwordParameter);
    }
}
