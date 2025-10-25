package org.pac4j.oidc.profile.azuread;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;
import org.pac4j.oidc.client.azuread.AzureAdIdTokenValidator;
import org.pac4j.oidc.client.azuread.AzureAdLogoutTokenValidator;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.TokenValidator;

/**
 * Specific token validator for AzureAD.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class AzureAdTokenValidator extends TokenValidator {

    /**
     * <p>Constructor for AzureAdTokenValidator.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     * @param metadata a {@link OIDCProviderMetadata} object
     */
    public AzureAdTokenValidator(final OidcConfiguration configuration, final OIDCProviderMetadata metadata) {
        super(configuration, metadata);
    }

    /** {@inheritDoc} */
    @Override
    protected IDTokenValidator createRSAIdTokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        return new AzureAdIdTokenValidator(super.createRSAIdTokenValidator(jwsAlgorithm, clientID));
    }

    @Override
    protected LogoutTokenValidator createRSALogoutTokenValidator(JWSAlgorithm jwsAlgorithm, ClientID clientID) {
        return new AzureAdLogoutTokenValidator(super.createRSALogoutTokenValidator(jwsAlgorithm, clientID));
    }
}
