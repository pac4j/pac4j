package org.pac4j.oidc.client;

import lombok.val;
import org.pac4j.oidc.config.AppleOidcConfiguration;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.pac4j.oidc.profile.apple.AppleProfile;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Apple.</p>
 * <p>More information at: https://developer.apple.com/documentation/sign_in_with_apple</p>
 *
 * @author Charley Wu
 * @since 5.0.0
 */
public class AppleClient extends OidcClient {

    /**
     * <p>Constructor for AppleClient.</p>
     */
    public AppleClient() {
    }

    /**
     * <p>Constructor for AppleClient.</p>
     *
     * @param configuration a {@link AppleOidcConfiguration} object
     */
    public AppleClient(AppleOidcConfiguration configuration) {
        super(configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        val profileCreator = new OidcProfileCreator(getConfiguration(), this);
        profileCreator.setProfileDefinition(new OidcProfileDefinition(x -> new AppleProfile()));
        setProfileCreatorIfUndefined(profileCreator);
        super.internalInit(forceReinit);
    }
}
