package org.pac4j.oidc.metadata;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.profile.azuread.AzureAdTokenValidator;
import org.pac4j.oidc.profile.creator.TokenValidator;

import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

/**
 * The metadata resolver for AzureAd.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Slf4j
public class AzureAdOpMetadataResolver extends OidcOpMetadataResolver {

    /**
     * <p>Constructor for AzureAdOpMetadataResolver.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     */
    public AzureAdOpMetadataResolver(final OidcConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected TokenValidator createTokenValidator() {
        return new AzureAdTokenValidator(configuration, this.loaded);
    }
    
    @Override
    protected OIDCProviderMetadata retrieveMetadata() {
        try (val in = SpringResourceHelper.getResourceInputStream(
            resource,
            null,
            configuration.getSslSocketFactory(),
            configuration.getHostnameVerifier(),
            configuration.getConnectTimeout(),
            configuration.getReadTimeout()
        )) {
            String metadata = IOUtils.readInputStreamToString(in);
            // When using the tenantid "common" an invalid issuer URL is returned containing {}
            // ULR encode the invalid characters so it wont break the azure flow
            metadata = metadata.replace("{tenantid}", "%7Btenantid%7D");
            return OIDCProviderMetadata.parse(metadata);
        } catch (final IOException | ParseException e) {
            throw new OidcException("Error getting OP metadata", e);
        }
    }
}
