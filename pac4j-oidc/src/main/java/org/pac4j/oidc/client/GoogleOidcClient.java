package org.pac4j.oidc.client;

import lombok.val;
import org.pac4j.core.logout.GoogleLogoutActionBuilder;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;
import org.pac4j.oidc.profile.google.GoogleOidcProfile;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Google.</p>
 * <p>More information at: https://developers.google.com/identity/protocols/OpenIDConnect</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class GoogleOidcClient extends OidcClient {

    /**
     * <p>Constructor for GoogleOidcClient.</p>
     */
    public GoogleOidcClient() {
    }

    /**
     * <p>Constructor for GoogleOidcClient.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     */
    public GoogleOidcClient(final OidcConfiguration configuration) {
        super(configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        getConfiguration().setDiscoveryURIIfUndefined("https://accounts.google.com/.well-known/openid-configuration");
        val profileCreator = new OidcProfileCreator(getConfiguration(), this);
        profileCreator.setProfileDefinition(new OidcProfileDefinition(x -> new GoogleOidcProfile()));
        setProfileCreatorIfUndefined(profileCreator);
        setLogoutActionBuilderIfUndefined(new GoogleLogoutActionBuilder());

        super.internalInit(forceReinit);
    }
}
