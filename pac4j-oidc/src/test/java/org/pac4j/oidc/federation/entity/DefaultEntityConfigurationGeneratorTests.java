package org.pac4j.oidc.federation.entity;

import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.federation.config.OidcFederationProperties;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link DefaultEntityConfigurationGenerator}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class DefaultEntityConfigurationGeneratorTests {

    private static DefaultEntityConfigurationGenerator newGenerator(OidcFederationProperties federation, String callbackUrl) {
        val config = new OidcConfiguration();
        config.setFederation(federation);

        val client = mock(OidcClient.class);
        when(client.getConfiguration()).thenReturn(config);
        when(client.getCallbackUrl()).thenReturn(callbackUrl);

        return new DefaultEntityConfigurationGenerator(client);
    }

    @Test
    public void testGetContentType() {
        val federation = new OidcFederationProperties();
        val generator = newGenerator(federation, "https://client.example.org/callback");
        assertEquals("application/entity-statement+jwt", generator.getContentType());
    }

    @Test
    public void testBuildConfigRequiresPrivateKeyPart() throws Exception {
        val federation = new OidcFederationProperties();
        val generator = newGenerator(federation, "https://client.example.org/callback");

        val signingKey = new RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .keyID("kid")
            .generate();

        val publicOnly = signingKey.toPublicJWK();
        assertThrows(TechnicalException.class, () -> generator.buildConfig(publicOnly));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildConfigGeneratesSignedJwtWithPublicJwks() throws Exception {
        val federation = new OidcFederationProperties();
        val generator = newGenerator(federation, "https://client.example.org/callback");

        val signingKey = new RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .keyID("kid")
            .generate();

        val serializedJwt = generator.buildConfig(signingKey);
        val signed = SignedJWT.parse(serializedJwt);

        assertEquals(DefaultEntityConfigurationGenerator.ENTITY_STATEMENT_TYPE, signed.getHeader().getType().toString());

        val jwksClaim = (Map<String, Object>) signed.getJWTClaimsSet().getClaim("jwks");
        assertNotNull(jwksClaim);

        val keys = (List<Map<String, Object>>) jwksClaim.get("keys");
        assertNotNull(keys);
        assertEquals(1, keys.size());

        // The published JWKS must only expose the public key.
        assertFalse(keys.get(0).containsKey("d"));
    }
}
