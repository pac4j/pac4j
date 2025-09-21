package org.pac4j.oidc.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.config.OidcConfiguration;

/**
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcOpMetadataResolverTest extends OidcOpMetadataResolverTestBase {

    @Test
    public void shouldUseFirstServerSupportedAuthMethod() throws URISyntaxException {
        OidcConfiguration configuration = getOidcConfiguration(null, "test");

        OidcOpMetadataResolver metadataResolver = getStaticMetadataResolver(configuration,
            List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST, ClientAuthenticationMethod.CLIENT_SECRET_BASIC));

        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_POST, metadataResolver.getClientAuthentication().getMethod());
    }

    @Test
    public void shouldRespectClientSupportedAuthMethod() throws URISyntaxException {
        OidcConfiguration configuration = getOidcConfiguration(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC), "test");

        OidcOpMetadataResolver metadataResolver = getStaticMetadataResolver(configuration,
            List.of(ClientAuthenticationMethod.PRIVATE_KEY_JWT, ClientAuthenticationMethod.CLIENT_SECRET_BASIC));

        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, metadataResolver.getClientAuthentication().getMethod());
    }

    @Test
    public void shouldFailInCaseOfNoCommonAuthMethod() throws URISyntaxException {
        OidcConfiguration oidcConfiguration = getOidcConfiguration(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC), "test");

        try {
            getStaticMetadataResolver(oidcConfiguration, List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST));
            fail("TechnicalException expected");
        } catch (TechnicalException e) {
            assertEquals("None of the Token endpoint provider metadata authentication methods are supported: [client_secret_post]",
                e.getMessage());
        }
    }


}
