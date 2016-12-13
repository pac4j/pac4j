package org.pac4j.oidc.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Keycloak.</p>
 * <p>This client needs two customParams in its configuration: </p>
 * <ul>
 *   <li>authUrl : Base auth server url, e.g., https://keycloak.example.com/auth </li>
 *   <li>realm : keycloak realm</li>
 * </ul>
 *
 * @author Julio Arrebola
 * @since 2.0.0
 */
public class KeycloakOidcClient extends OidcClient<KeycloakOidcProfile> {

    public KeycloakOidcClient() {
    }

    public KeycloakOidcClient(final OidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", getConfiguration());
        String baseUri = getConfiguration().getCustomParam("authUrl");
        CommonHelper.assertNotNull("authUrl", baseUri);
        String realm = getConfiguration().getCustomParam("realm");
        CommonHelper.assertNotNull("realm", realm);
        getConfiguration().setDiscoveryURI(baseUri+"/realms/"+realm+"/.well-known/openid-configuration");
        final OidcProfileCreator<KeycloakOidcProfile> profileCreator = new OidcProfileCreator<>(getConfiguration());
        profileCreator.setProfileDefinition(new OidcProfileDefinition<>(x -> new KeycloakOidcProfile()));
        setProfileCreator(profileCreator);
        //setLogoutActionBuilder(new GoogleLogoutActionBuilder<>());

        super.internalInit(context);
    }
}