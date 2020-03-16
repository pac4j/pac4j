package org.pac4j.oidc.profile.azuread;

import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.OidcProfileCreator;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Specific profile creator for Azure.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class AzureAdProfileCreator extends OidcProfileCreator {

    public AzureAdProfileCreator(final OidcConfiguration configuration, final OidcClient client) {
        super(configuration, client);
    }

    @Override
    protected void internalInit() {
        assertNotNull("configuration", configuration);

        if (configuration.getTokenValidator() == null) {
            configuration.setTokenValidator(new AzureAdTokenValidator(configuration));
        }

        defaultProfileDefinition(new AzureAdProfileDefinition());

        super.internalInit();
    }
}
