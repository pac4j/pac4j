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

    /**
     * <p>Constructor for AzureAdProfileCreator.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     * @param client a {@link OidcClient} object
     */
    public AzureAdProfileCreator(final OidcConfiguration configuration, final OidcClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("configuration", configuration);

        setProfileDefinitionIfUndefined(new AzureAdProfileDefinition());

        super.internalInit(forceReinit);
    }
}
