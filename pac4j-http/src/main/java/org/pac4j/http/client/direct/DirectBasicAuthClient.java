package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This class is the client to authenticate users directly through HTTP basic auth.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DirectBasicAuthClient extends DirectClient<UsernamePasswordCredentials> {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public DirectBasicAuthClient() {
    }

    public DirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator) {
        defaultAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator,
                                 final ProfileCreator profileCreator) {
        defaultAuthenticator(usernamePasswordAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        assertNotBlank("realmName", this.realmName);

        defaultCredentialsExtractor(new BasicAuthExtractor());
    }

    @Override
    protected Optional<UsernamePasswordCredentials> retrieveCredentials(final WebContext context) {
        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Basic realm=\"" + realmName + "\"");

        return super.retrieveCredentials(context);
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(final String realmName) {
        this.realmName = realmName;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "authorizationGenerators", getAuthorizationGenerators(), "realmName", this.realmName);
    }
}
