package org.pac4j.oidc.metadata;

import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.pac4j.oidc.profile.creator.TokenValidator;

/**
 * OIDC OP metadata resolver.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public interface IOidcOpMetadataResolver {

    OIDCProviderMetadata load();

    TokenValidator getTokenValidator();

    ClientAuthentication getClientAuthenticationTokenEndpoint();

    @Deprecated
    default ClientAuthentication getClientAuthentication() {
        return getClientAuthenticationTokenEndpoint();
    }

    ClientAuthentication getClientAuthenticationPAREndpoint();
}
