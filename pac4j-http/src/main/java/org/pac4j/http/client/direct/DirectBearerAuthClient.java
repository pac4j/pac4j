package org.pac4j.http.client.direct;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.toNiceString;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.BearerAuthExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;

import java.util.Optional;

/**
 * <p>This class is the client to authenticate users directly through RFC 6750 HTTP bearer authentication.</p>
 *
 * @author Graham Leggett
 * @since 3.5.0
 */
public class DirectBearerAuthClient extends DirectClient {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public DirectBearerAuthClient() {
    }

    public DirectBearerAuthClient(final Authenticator tokenAuthenticator) {
        defaultAuthenticator(tokenAuthenticator);
    }

    public DirectBearerAuthClient(final ProfileCreator profileCreator) {
        defaultAuthenticator(Authenticator.ALWAYS_VALIDATE);
        defaultProfileCreator(profileCreator);
    }

    public DirectBearerAuthClient(final Authenticator tokenAuthenticator,
                                 final ProfileCreator profileCreator) {
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);

        defaultCredentialsExtractor(new BearerAuthExtractor());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context, final SessionStore sessionStore) {
        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, HttpConstants.BEARER_HEADER_PREFIX + "realm=\"" + realmName + "\"");

        return super.retrieveCredentials(context, sessionStore);
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
