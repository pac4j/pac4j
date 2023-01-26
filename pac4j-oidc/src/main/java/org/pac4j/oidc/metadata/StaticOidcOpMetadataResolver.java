package org.pac4j.oidc.metadata;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.TokenValidator;

/**
 * An OP metadata resolver with static metadata.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public class StaticOidcOpMetadataResolver extends OidcOpMetadataResolver {

    private final OIDCProviderMetadata staticMetadata;

    public StaticOidcOpMetadataResolver(final OidcConfiguration configuration, final OIDCProviderMetadata staticMetadata) {
        super(configuration);
        this.staticMetadata = staticMetadata;
    }

    @Override
    protected void internalLoad() {
        this.loaded = staticMetadata;

        this.clientAuthentication = computeClientAuthentication();

        this.tokenValidator = new TokenValidator(configuration, this.loaded);
    }
}
