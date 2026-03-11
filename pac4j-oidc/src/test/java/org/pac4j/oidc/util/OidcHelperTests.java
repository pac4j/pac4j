package org.pac4j.oidc.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link OidcHelper}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class OidcHelperTests {

    @Test
    public void testMatchRPAlgAgainstOPAlgsKeepsRpAlgorithmWhenAvailable() {
        val opAlgs = List.of(JWSAlgorithm.HS256, JWSAlgorithm.RS256);

        val keptAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS256, opAlgs);

        assertEquals(List.of(JWSAlgorithm.RS256), keptAlgs);
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsReturnsOpAlgorithmsWhenRpAlgorithmIsUnavailable() {
        val opAlgs = List.of(JWSAlgorithm.HS256, JWSAlgorithm.RS256);

        val keptAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS512, opAlgs);

        assertSame(opAlgs, keptAlgs);
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsReturnsOpAlgorithmsWhenRpAlgorithmIsNull() {
        val opAlgs = List.of(JWSAlgorithm.HS256, JWSAlgorithm.RS256);

        val keptAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("signing", null, opAlgs);

        assertSame(opAlgs, keptAlgs);
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsThrowsOnEmptyOpAlgorithms() {
        val exception = assertThrows(TechnicalException.class,
            () -> OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS256, List.of()));

        assertEquals("There must at least one signing JWS algorithm supported on the OP side", exception.getMessage());
    }

    @Test
    public void testMatchRPAlgAgainstOPAlgsThrowsOnNullOpAlgorithms() {
        val exception = assertThrows(TechnicalException.class,
            () -> OidcHelper.matchRPAlgAgainstOPAlgs("signing", JWSAlgorithm.RS256, null));

        assertEquals("opAlgs cannot be null", exception.getMessage());
    }

    @Test
    public void testRetrieveJwkSetFromUsesEmbeddedMetadataJwkSetWhenPresent() throws Exception {
        val embeddedKey = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("embedded-kid").generate();
        val embeddedJwkSet = new JWKSet(embeddedKey.toPublicJWK());
        val metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getJWKSet()).thenReturn(embeddedJwkSet);

        val jwkSet = OidcHelper.retrieveJwkSetFrom(metadata, "unused-default-url");

        assertSame(embeddedJwkSet, jwkSet);
        verify(metadata, never()).getJWKSetURI();
    }

    @Test
    public void testRetrieveJwkSetFromLoadsFromMetadataJwkSetUriWhenNeeded() throws Exception {
        val jwksPath = writePublicJwkSetToTempFile("uri-kid");
        val metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getJWKSet()).thenReturn(null);
        when(metadata.getJWKSetURI()).thenReturn(jwksPath.toUri());

        val jwkSet = OidcHelper.retrieveJwkSetFrom(metadata, null);

        assertEquals(1, jwkSet.getKeys().size());
        assertEquals("uri-kid", jwkSet.getKeys().get(0).getKeyID());
    }

    @Test
    public void testRetrieveJwkSetFromFallsBackToDefaultUrlWhenMetadataHasNoKeys() throws Exception {
        val jwksPath = writePublicJwkSetToTempFile("default-kid");
        val metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getJWKSet()).thenReturn(null);
        when(metadata.getJWKSetURI()).thenReturn(null);

        val jwkSet = OidcHelper.retrieveJwkSetFrom(metadata, jwksPath.toString());

        assertEquals(1, jwkSet.getKeys().size());
        assertEquals("default-kid", jwkSet.getKeys().get(0).getKeyID());
    }

    @Test
    public void testRetrieveJwkSetFromThrowsWhenNoSourceIsAvailable() {
        val exception = assertThrows(TechnicalException.class, () -> OidcHelper.retrieveJwkSetFrom(null, null));
        assertEquals("Unable to retrieve keys from JWK", exception.getMessage());
    }

    private static Path writePublicJwkSetToTempFile(String kid) throws Exception {
        val jwksPath = Files.createTempDirectory("jwks-helper-tests").resolve(kid + ".jwks");
        val key = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID(kid).generate();
        val jwkSet = new JWKSet(key.toPublicJWK());
        Files.writeString(jwksPath, jwkSet.toString(false));
        return jwksPath;
    }
}
