package org.pac4j.oidc.client;

import java.util.Map;

import org.pac4j.core.client.IndirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;
import org.pac4j.oidc.credentials.extractor.OidcExtractor;
import org.pac4j.oidc.profile.OidcProfile;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
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
public class OidcClient<U extends OidcProfile> extends IndirectClientV2<OidcCredentials, U> {

    private OidcConfiguration configuration = new OidcConfiguration();

    public OidcClient() { }

    public OidcClient(final OidcConfiguration oidcConfiguration) {
        this.configuration = oidcConfiguration;
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OidcConfiguration oidcConfiguration) {
        this.configuration = oidcConfiguration;
    }

    @Deprecated
    public OidcClient(final String clientId, final String secret, final String discoveryURI) {
        configuration.setClientId(clientId);
        configuration.setSecret(secret);
        configuration.setDiscoveryURI(discoveryURI);
    }

    @Deprecated
    public void setDiscoveryURI(final String discoveryURI) {
        configuration.setDiscoveryURI(discoveryURI);
    }

    @Deprecated
    public void setClientID(final String clientId) {
        configuration.setClientId(clientId);
    }

    @Deprecated
    public void setSecret(final String secret) {
        configuration.setSecret(secret);
    }

    @Deprecated
    public void setScope(final String scope) {
        configuration.setScope(scope);
    }

    @Deprecated
    public void addCustomParam(final String key, final String value) {
        configuration.addCustomParam(key, value);
    }

    @Deprecated
    public void setCustomParams(final Map<String, String> customParams) {
        configuration.setCustomParams(customParams);
    }

    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);

        CommonHelper.assertNotNull("configuration", configuration);
        configuration.setCallbackUrl(computeFinalCallbackUrl(context));
        configuration.init(context);

        setRedirectActionBuilder(new OidcRedirectActionBuilder(configuration));
        setCredentialsExtractor(new OidcExtractor(configuration, getName()));
        setAuthenticator(new OidcAuthenticator(configuration));
        createProfileCreator();
    }

    protected void createProfileCreator() {
        setProfileCreator(new OidcProfileCreator<>(configuration));
    }

    @Deprecated
    public void setPreferredJwsAlgorithm(final JWSAlgorithm preferredJwsAlgorithm) {
        configuration.setPreferredJwsAlgorithm(preferredJwsAlgorithm);
    }

    @Deprecated
    public void setUseNonce(final boolean useNonce) {
        configuration.setUseNonce(useNonce);
    }

    @Deprecated
    public void setClientAuthenticationMethod(final ClientAuthenticationMethod clientAuthenticationMethod) {
        configuration.setClientAuthenticationMethod(clientAuthenticationMethod);
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", getAjaxRequestResolver(),
                "redirectActionBuilder", getRedirectActionBuilder(), "credentialsExtractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(), "configuration", configuration);
    }
}
