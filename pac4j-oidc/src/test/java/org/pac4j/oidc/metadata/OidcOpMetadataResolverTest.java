package org.pac4j.oidc.metadata;

import java.net.URISyntaxException;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.config.PrivateKeyJWTClientAuthnMethodConfig;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;

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

    @Test
    public void testExpiredPrivateJWKShouldRecreateNewkey() throws Exception {

        OidcConfiguration configuration = getOidcConfiguration(Set.of(ClientAuthenticationMethod.PRIVATE_KEY_JWT), "test");

        //Creates a keypair generator to create a keypair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom();
        keyPairGenerator.initialize(2048, secureRandom);

        // Generate the keypair
        var keyPair = keyPairGenerator.generateKeyPair();

        // Gets the private key
        var privateKey = keyPair.getPrivate();

        PrivateKeyJWTClientAuthnMethodConfig privateKeyJWTClientAuthnMethodConfig = 
            new PrivateKeyJWTClientAuthnMethodConfig(JWSAlgorithm.RS256, privateKey);

        // Sets validity to 2 seconds
        privateKeyJWTClientAuthnMethodConfig.setValidity(2L);

        // Disable clock skew for faster test
        privateKeyJWTClientAuthnMethodConfig.setKeyClockSkew(0);
        configuration.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJWTClientAuthnMethodConfig);

        OidcOpMetadataResolver metadataResolver = getStaticMetadataResolverWithTokenEndPoint(configuration,
            List.of(ClientAuthenticationMethod.PRIVATE_KEY_JWT));

        ClientAuthentication authentication = metadataResolver.getClientAuthentication();
        ClientAuthentication authenticationBeforeExp = metadataResolver.getClientAuthentication();
        // Gets client auth 2 times, must be the same
        assertEquals(authentication, authenticationBeforeExp);

        Thread.sleep(2200);
        ClientAuthentication authenticationAfterExp = metadataResolver.getClientAuthentication();
        // After Expiration the token must be recreated
        assertNotEquals(authentication, authenticationAfterExp);
        Thread.sleep(500);
        // After smaller timeout the token should be the same
        ClientAuthentication authenticationAfterExp2 = metadataResolver.getClientAuthentication();
        assertEquals(authenticationAfterExp, authenticationAfterExp2);
    }
}
