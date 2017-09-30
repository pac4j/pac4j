package org.pac4j.oidc.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;
import org.pac4j.oidc.credentials.extractor.OidcExtractor;
import org.pac4j.oidc.logout.OidcLogoutActionBuilder;
import org.pac4j.oidc.profile.OidcProfile;

import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.redirect.OidcRedirectActionBuilder;

/**
 * This class is the client to authenticate users with an OpenID Connect 1.0 provider.
 * By default, this implementation relies on the
 * "code" response type. (http://openid.net/specs/openid-connect-core-1_0.html).
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
public class OidcClient<U extends OidcProfile> extends IndirectClient<OidcCredentials, U> {

    private OidcConfiguration configuration = new OidcConfiguration();

    public OidcClient() { }

    public OidcClient(final OidcConfiguration configuration) {
        setConfiguration(configuration);
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.init();

        defaultRedirectActionBuilder(new OidcRedirectActionBuilder(configuration, getName(), getCallbackUrl(), getCallbackUrlResolver()));
        defaultCredentialsExtractor(new OidcExtractor(configuration, getName(), getCallbackUrl(), getCallbackUrlResolver()));
        defaultAuthenticator(new OidcAuthenticator(configuration, getCallbackUrl(), getCallbackUrlResolver()));
        defaultProfileCreator(new OidcProfileCreator<>(configuration));
        defaultLogoutActionBuilder(new OidcLogoutActionBuilder<U>(configuration));
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "callbackUrlResolver", getCallbackUrlResolver(), "ajaxRequestResolver", getAjaxRequestResolver(),
                "redirectActionBuilder", getRedirectActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
                "logoutActionBuilder", getLogoutActionBuilder(), "configuration", configuration);
    }
}
