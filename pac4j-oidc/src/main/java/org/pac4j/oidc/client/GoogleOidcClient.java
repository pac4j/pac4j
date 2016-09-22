package org.pac4j.oidc.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.profile.google.GoogleOidcProfile;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Google.</p>
 * <p>More information at: https://developers.google.com/identity/protocols/OpenIDConnect</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class GoogleOidcClient extends OidcClient<GoogleOidcProfile> {

    public GoogleOidcClient() {
    }

    public GoogleOidcClient(final OidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", getConfiguration());
        getConfiguration().setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");

        super.internalInit(context);
    }

    @Override
    protected void createProfileCreator() {
        final OidcProfileCreator<GoogleOidcProfile> profileCreator = new OidcProfileCreator<>(getConfiguration());
        profileCreator.setProfileFactory(GoogleOidcProfile::new);
        setProfileCreator(profileCreator);
    }
}
