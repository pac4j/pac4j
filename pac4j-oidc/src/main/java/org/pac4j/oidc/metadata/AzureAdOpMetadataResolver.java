package org.pac4j.oidc.metadata;

import lombok.extern.slf4j.Slf4j;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.azuread.AzureAdTokenValidator;

/**
 * The metadata resolver for AzureAd.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Slf4j
public class AzureAdOpMetadataResolver extends OidcOpMetadataResolver {

    public AzureAdOpMetadataResolver(final OidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void internalLoad() {
        this.loaded = retrieveMetadata();

        this.clientAuthentication = computeClientAuthentication();

        this.tokenValidator = new AzureAdTokenValidator(configuration, this.loaded);
    }
}
