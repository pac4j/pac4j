package org.pac4j.oidc.profile.azuread;

import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;

/**
 * Specific profile creator for Azure.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class AzureAdProfileCreator extends OidcProfileCreator<AzureAdProfile> {

    public AzureAdProfileCreator(final OidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void internalInit() {

        tokenValidator = new AzureAdTokenValidator(configuration);

        defaultProfileDefinition(new AzureAdProfileDefinition());

        super.internalInit();
    }
}
