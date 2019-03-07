package org.pac4j.http.client.direct;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.toNiceString;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
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
public class DirectBearerAuthClient extends DirectClient<TokenCredentials> {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public DirectBearerAuthClient() {
    }

    public DirectBearerAuthClient(final Authenticator tokenAuthenticator) {
        defaultAuthenticator(tokenAuthenticator);
    }

    public DirectBearerAuthClient(final Authenticator tokenAuthenticator,
                                 final ProfileCreator profileCreator) {
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        assertNotBlank("realmName", this.realmName);

        defaultCredentialsExtractor(new BearerAuthExtractor());
    }

    @Override
    protected Optional<TokenCredentials> retrieveCredentials(final WebContext context) {
        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, HttpConstants.BEARER_HEADER_PREFIX + "realm=\"" + realmName + "\"");

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
