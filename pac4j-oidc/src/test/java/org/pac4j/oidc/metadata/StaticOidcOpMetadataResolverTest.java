package org.pac4j.oidc.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.pac4j.oidc.config.OidcConfiguration;


/**
 * @author Mathias Loesch
 * @since 6.0.4
 */
public class StaticOidcOpMetadataResolverTest extends OidcOpMetadataResolverTestBase {

    @Test
    public void shouldAllowNullDiscoveryURI() throws URISyntaxException {
        OidcConfiguration oidcConfiguration = getOidcConfiguration(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_POST), null);

        StaticOidcOpMetadataResolver staticMetadataResolver = getStaticMetadataResolver(oidcConfiguration,
            List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST));

        OIDCProviderMetadata expectedMetadata = getOidcProviderMetadata(List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST));
        assertEquals(staticMetadataResolver.load().toString(), expectedMetadata.toString());

    }
}
