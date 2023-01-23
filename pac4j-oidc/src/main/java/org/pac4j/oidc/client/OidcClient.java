package org.pac4j.oidc.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
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

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * This class is the client to authenticate users with an OpenID Connect 1.0 provider.
 * By default, this implementation relies on the
 * "code" response type. (http://openid.net/specs/openid-connect-core-1_0.html).
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
@ToString(callSuper = true)
public class OidcClient extends IndirectClient {

    @Getter
    @Setter
    private OidcConfiguration configuration;

    public OidcClient() { }

    public OidcClient(final OidcConfiguration configuration) {
        setConfiguration(configuration);
    }

    @Override
    protected void beforeInternalInit(final boolean forceReinit) {
        super.beforeInternalInit(forceReinit);
        assertNotNull("configuration", configuration);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.init(forceReinit);

        setRedirectionActionBuilderIfUndefined(new OidcRedirectionActionBuilder(this));
        setCredentialsExtractorIfUndefined(new OidcExtractor(configuration, this));
        setAuthenticatorIfUndefined(new OidcAuthenticator(configuration, this));
        setProfileCreatorIfUndefined(new OidcProfileCreator(configuration, this));
        setLogoutActionBuilderIfUndefined(new OidcLogoutActionBuilder(configuration));
    }

    @Override
    public Optional<UserProfile> renewUserProfile(final CallContext ctx, final UserProfile profile) {
        val oidcProfile = (OidcProfile) profile;
        val refreshToken = oidcProfile.getRefreshToken();
        if (refreshToken != null) {
            val credentials = new OidcCredentials();
            credentials.setRefreshToken(refreshToken);
            val authenticator = new OidcAuthenticator(getConfiguration(), this);
            authenticator.refresh(credentials);

            // Create a profile if the refresh grant was successful
            if (credentials.getAccessToken() != null) {
                return getUserProfile(ctx, credentials);
            }
        }

        return Optional.empty();
    }

    @Override
    public void notifySessionRenewal(final CallContext ctx, final String oldSessionId) {
        configuration.findSessionLogoutHandler().renewSession(ctx, oldSessionId);
    }
}
