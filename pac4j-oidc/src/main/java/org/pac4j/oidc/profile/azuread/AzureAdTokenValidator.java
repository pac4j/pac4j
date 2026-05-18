package org.pac4j.oidc.profile.azuread;

import org.pac4j.oidc.client.azuread.AzureAdIdTokenValidator;
import org.pac4j.oidc.client.azuread.AzureAdLogoutTokenValidator;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.TokenValidator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;

/**
 * Specific token validator for AzureAD.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class AzureAdTokenValidator extends TokenValidator {

    public AzureAdTokenValidator(final OidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected IDTokenValidator createRSAIdTokenValidator(final OidcConfiguration configuration,
                                                       final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        return new AzureAdIdTokenValidator(super.createRSAIdTokenValidator(configuration, jwsAlgorithm, clientID));
    }
    
    @Override
    protected LogoutTokenValidator createRSALogoutTokenValidator(final OidcConfiguration configuration,
    													JWSAlgorithm jwsAlgorithm, ClientID clientID) {
        return new AzureAdLogoutTokenValidator(super.createRSALogoutTokenValidator(configuration, jwsAlgorithm, clientID));
    }
}
