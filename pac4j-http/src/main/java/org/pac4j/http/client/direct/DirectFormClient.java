package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.extractor.FormExtractor;

/**
 * This class is the client to authenticate users, based on form HTTP parameters.
 *
 * @author Jerome Leleu
 * @since 1.8.6
 */
public class DirectFormClient extends DirectClient<UsernamePasswordCredentials> {

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public DirectFormClient() {}

    public DirectFormClient(final Authenticator usernamePasswordAuthenticator) {
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectFormClient(final String usernameParameter, final String passwordParameter,
                            final Authenticator usernamePasswordAuthenticator) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectFormClient(final Authenticator usernamePasswordAuthenticator,
                            final ProfileCreator profileCreator) {
        defaultAuthenticator(usernamePasswordAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotBlank("usernameParameter", usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", passwordParameter);

        defaultCredentialsExtractor(new FormExtractor(usernameParameter, passwordParameter));
    }

    public String getUsernameParameter() {
        return this.usernameParameter;
    }

    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return this.passwordParameter;
    }

    public void setPasswordParameter(final String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "usernameParameter", this.usernameParameter,
                "passwordParameter", this.passwordParameter, "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}
