package org.pac4j.oidc.profile.azuread;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import org.pac4j.oidc.client.azuread.AzureAdIdTokenValidator;
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
        defaultProfileDefinition(new AzureAdProfileDefinition());
        super.internalInit();
    }

    @Override
    protected IDTokenValidator createRSATokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        return new AzureAdIdTokenValidator(super.createRSATokenValidator(jwsAlgorithm, clientID));
    }
}
