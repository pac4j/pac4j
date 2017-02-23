package org.pac4j.oidc.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.KeycloakOidcConfiguration;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Keycloak.</p>
 * <p>A KeycloakOidcConfiguration is needed to create a client with to additional properties: </p>
 * <ul>
 *   <li>baseUri : Base auth server url, e.g., https://keycloak.example.com/auth </li>
 *   <li>realm : keycloak realm</li>
 * </ul>
 * <p> As seen in test case org.pac4j.oidc.run.RunKeycloakOidcClient</p>
 *
 * @author Julio Arrebola
 * @since 2.0.0
 */
public class KeycloakOidcClient extends OidcClient<KeycloakOidcProfile> {

    public KeycloakOidcClient() {
    }

    public KeycloakOidcClient(final KeycloakOidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", getConfiguration());
        final OidcProfileCreator<KeycloakOidcProfile> profileCreator = new OidcProfileCreator<>(getConfiguration());
        profileCreator.setProfileDefinition(new OidcProfileDefinition<>(x -> new KeycloakOidcProfile()));
        defaultProfileCreator(profileCreator);

        super.clientInit(context);
    }
}