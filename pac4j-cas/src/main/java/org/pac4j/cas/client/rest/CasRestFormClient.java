package org.pac4j.cas.client.rest;

import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.util.CommonHelper;

/**
 * Direct client which receives credentials as form parameters and validates them via the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestFormClient extends AbstractCasRestClient {

    private String casServerPrefixUrl;

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public CasRestFormClient() {}

    public CasRestFormClient(final String casServerPrefixUrl) {
        this.casServerPrefixUrl = casServerPrefixUrl;
    }

    public CasRestFormClient(final Authenticator authenticator) {
        setAuthenticator(authenticator);
    }

    public CasRestFormClient(final String casServerPrefixUrl, final String usernameParameter, final String passwordParameter) {
        this.casServerPrefixUrl = casServerPrefixUrl;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    public CasRestFormClient(final Authenticator authenticator, final String usernameParameter, final String passwordParameter) {
        setAuthenticator(authenticator);
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("usernameParameter", this.usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", this.passwordParameter);

        setCredentialsExtractor(new FormExtractor(this.usernameParameter, this.passwordParameter, getName()));
        if (CommonHelper.isNotBlank(this.casServerPrefixUrl)) {
            setAuthenticator(new CasRestAuthenticator(this.casServerPrefixUrl));
        }
    }

    public String getCasServerPrefixUrl() {
        return casServerPrefixUrl;
    }

    public void setCasServerPrefixUrl(String casServerPrefixUrl) {
        this.casServerPrefixUrl = casServerPrefixUrl;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    @Deprecated
    public String getUsername() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    @Deprecated
    public String getPassword() {
        return passwordParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "usernameParameter", this.usernameParameter,
                "passwordParameter", this.passwordParameter, "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}
