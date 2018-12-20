package org.pac4j.oidc.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;
import org.pac4j.oidc.credentials.extractor.OidcExtractor;

import org.pac4j.oidc.logout.OidcLogoutActionBuilder;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.redirect.OidcRedirectionActionBuilder;

/**
 * This class is the client to authenticate users with an OpenID Connect 1.0 provider.
 * By default, this implementation relies on the
 * "code" response type. (http://openid.net/specs/openid-connect-core-1_0.html).
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
public class OidcClient<V extends OidcConfiguration> extends IndirectClient<OidcCredentials> {

    private V configuration = null;

    public OidcClient() { }

    public OidcClient(final V configuration) {
        setConfiguration(configuration);
    }

    public V getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final V configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.init();

        defaultRedirectionActionBuilder(new OidcRedirectionActionBuilder(configuration, this));
        defaultCredentialsExtractor(new OidcExtractor(configuration, this));
        defaultAuthenticator(new OidcAuthenticator(configuration, this));
        defaultProfileCreator(new OidcProfileCreator(configuration));
        defaultLogoutActionBuilder(new OidcLogoutActionBuilder(configuration));
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
            "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", getAjaxRequestResolver(),
            "redirectionActionBuilder", getRedirectionActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "logoutActionBuilder", getLogoutActionBuilder(), "authorizationGenerators", getAuthorizationGenerators(),
            "configuration", configuration);
    }
}
