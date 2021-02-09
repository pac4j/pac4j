package org.pac4j.oidc.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;
import org.pac4j.oidc.credentials.extractor.OidcExtractor;

import org.pac4j.oidc.logout.OidcLogoutActionBuilder;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.redirect.OidcRedirectionActionBuilder;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * This class is the client to authenticate users with an OpenID Connect 1.0 provider.
 * By default, this implementation relies on the
 * "code" response type. (http://openid.net/specs/openid-connect-core-1_0.html).
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
public class OidcClient extends IndirectClient {

    private OidcConfiguration configuration;

    public OidcClient() { }

    public OidcClient(final OidcConfiguration configuration) {
        setConfiguration(configuration);
    }

    @Override
    protected void beforeInternalInit() {
        super.beforeInternalInit();
        assertNotNull("configuration", configuration);
    }

    @Override
    protected void internalInit() {
        configuration.init();

        defaultRedirectionActionBuilder(new OidcRedirectionActionBuilder(configuration, this));
        defaultCredentialsExtractor(new OidcExtractor(configuration, this));
        defaultAuthenticator(new OidcAuthenticator(configuration, this));
        defaultProfileCreator(new OidcProfileCreator(configuration, this));
        defaultLogoutActionBuilder(new OidcLogoutActionBuilder(configuration));
    }

    @Override
    public Optional<UserProfile> renewUserProfile(final UserProfile profile, final WebContext context, final SessionStore sessionStore) {
        var oidcProfile = (OidcProfile) profile;
        var refreshToken = oidcProfile.getRefreshToken();
        if (refreshToken != null) {
            var credentials = new OidcCredentials();
            credentials.setRefreshToken(refreshToken);
            var authenticator = new OidcAuthenticator(getConfiguration(), this);
            authenticator.refresh(credentials);

            // Create a profile if the refresh grant was successful
            if (credentials.getAccessToken() != null) {
                return getUserProfile(credentials, context, sessionStore);
            }
        }

        return Optional.empty();
    }

    @Override
    public void notifySessionRenewal(final String oldSessionId, final WebContext context, final SessionStore sessionStore) {
        configuration.findLogoutHandler().renewSession(oldSessionId, context, sessionStore);
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
            "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", getAjaxRequestResolver(),
            "redirectionActionBuilder", getRedirectionActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "logoutActionBuilder", getLogoutActionBuilder(), "authorizationGenerators", getAuthorizationGenerators(),
            "configuration", configuration);
    }
}
