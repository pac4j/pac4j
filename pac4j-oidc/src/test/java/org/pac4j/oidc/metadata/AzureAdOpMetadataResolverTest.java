package org.pac4j.oidc.metadata;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.pac4j.oidc.config.AzureAd2OidcConfiguration;

/**
 * Tests {@link AzureAdOpMetadataResolver}.
 * 
 * @author Arjan van de Kamp
 * @since 6.0.4
 */
public class AzureAdOpMetadataResolverTest extends OidcOpMetadataResolverTestBase {

    @Test
    public void testIfIssuerDoesNotContainBraces() throws URISyntaxException, UnsupportedEncodingException {
        AzureAd2OidcConfiguration oidcConfiguration = getAzureAd2OidcConfiguration(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_POST, 
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC));
        oidcConfiguration.init();
        
        AzureAdOpMetadataResolver azureAdOpMetadataResolver = getAzureAdOpMetadataResolver(oidcConfiguration,
            List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST));

        Issuer issuer = azureAdOpMetadataResolver.load().getIssuer();
        
        assertNotNull(issuer);
        assertFalse(containsCharacter(issuer.toString(), '}'), "Issuer contains '}'");
        assertFalse(containsCharacter(issuer.toString(), '{'), "Issuer contains '{'");
    }
    
    public boolean containsCharacter(String string, char character) {
        return string.indexOf(character) != -1;
    }
}
