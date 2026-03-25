package org.pac4j.oidc.metadata.chain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityMetadataValidator;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainResolver;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.federation.config.OidcTrustAnchorProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link FederationChainResolver}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class FederationChainResolverTests {

    private static final String OP_ENTITY_ID = "https://op.example.org";
    private static final String RP_ENTITY_ID = "https://rp.example.org";
    private static final String TRUST_ANCHOR_ISSUER = "https://ta.example.org";
    private static final String METADATA_CLIENT_SECRET_BASIC = """
        {
          "issuer": "https://op.example.org",
          "authorization_endpoint": "https://op.example.org/authorize",
          "token_endpoint": "https://op.example.org/token",
          "jwks_uri": "https://op.example.org/jwks",
          "response_types_supported": ["code"],
          "subject_types_supported": ["public"],
          "id_token_signing_alg_values_supported": ["RS256"],
          "token_endpoint_auth_methods_supported": ["client_secret_basic"]
        }
        """;

    @TempDir
    public Path tmp;

    private OidcConfiguration configuration;
    private FederationChainResolver resolver;

    @BeforeEach
    public void beforeEach() throws Exception {
        configuration = new OidcConfiguration();
        configuration.getFederation().setTargetOp(OP_ENTITY_ID);
        configuration.getFederation().setEntityId(RP_ENTITY_ID);

        val trustAnchorKey = new RSAKeyGenerator(2048).keyID("ta-key").generate();
        val trustAnchorJwks = new JWKSet(trustAnchorKey.toPublicJWK());
        val trustAnchorJwksFile = tmp.resolve("trust-anchor.jwks");
        Files.writeString(trustAnchorJwksFile, trustAnchorJwks.toString(false));
        configuration.getFederation().setTrustAnchors(List.of(
            new OidcTrustAnchorProperties(TRUST_ANCHOR_ISSUER, trustAnchorJwksFile.toString())));

        resolver = new FederationChainResolver();
    }

    @Test
    public void testLoadTrustAnchorsFailsWhenNoAnchorsAreConfigured() {
        configuration.getFederation().setTrustAnchors(List.of());

        val exception = assertThrows(OidcConfigurationException.class, () -> resolver.loadTrustAnchors(configuration));
        assertEquals("No trust anchors defined", exception.getMessage());
    }

    @Test
    public void testLoadTrustAnchorsLoadsConfiguredJwks() {
        val anchors = resolver.loadTrustAnchors(configuration);

        assertEquals(1, anchors.size());
        val loadedAnchorJwks = anchors.get(new EntityID(TRUST_ANCHOR_ISSUER));
        assertEquals("ta-key", loadedAnchorJwks.getKeys().get(0).getKeyID());
    }

    @Test
    public void testResolveReturnsOpResolutionWhenTrustChainIsNotRequested() throws Exception {
        val expectedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val expectedExpirationTime = new Date(System.currentTimeMillis() + 60_000);
        val expectedJwks = new JWKSet();
        val expectedResolution = new FederationChainResolver.ResolutionResult(
            expectedMetadata, expectedExpirationTime, expectedJwks, null);

        val opChain = Mockito.mock(TrustChain.class);
        val resolvedEntities = new ArrayList<String>();
        val testResolver = new FederationChainResolver() {
            @Override
            protected TrustChain resolve(final TrustChainResolver resolver,
                                         final EntityMetadataValidator validator,
                                         final EntityID entityID) {
                resolvedEntities.add(entityID.getValue());
                return opChain;
            }

            @Override
            protected ResolutionResult getResolvedProviderMetadata(final TrustChain chain) {
                assertSame(opChain, chain);
                return expectedResolution;
            }
        };

        val result = testResolver.resolve(configuration);
        assertSame(expectedMetadata, result.metadata());
        assertEquals(expectedExpirationTime, result.expirationTime());
        assertSame(expectedJwks, result.federationJWKS());
        assertNull(result.trustChain());
        assertEquals(List.of(OP_ENTITY_ID), resolvedEntities);
    }

    @Test
    public void testResolveIncludesRpTrustChainAndKeepsEarliestExpiration() throws Exception {
        configuration.getFederation().setSendTrustChain(true);

        val expectedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val now = System.currentTimeMillis();
        val opExpirationTime = new Date(now + 120_000);
        val rpExpirationTime = new Date(now + 60_000);
        val expectedJwks = new JWKSet();
        val opResolution = new FederationChainResolver.ResolutionResult(
            expectedMetadata, opExpirationTime, expectedJwks, null);

        val opChain = Mockito.mock(TrustChain.class);
        val rpChain = Mockito.mock(TrustChain.class);
        Mockito.when(rpChain.resolveExpirationTime()).thenReturn(rpExpirationTime);
        Mockito.when(rpChain.toSerializedJWTs()).thenReturn(List.of("rp-entity-statement"));

        val resolvedEntities = new ArrayList<String>();
        val testResolver = new FederationChainResolver() {
            @Override
            protected TrustChain resolve(final TrustChainResolver resolver,
                                         final EntityMetadataValidator validator,
                                         final EntityID entityID) {
                resolvedEntities.add(entityID.getValue());
                if (OP_ENTITY_ID.equals(entityID.getValue())) {
                    return opChain;
                }
                return rpChain;
            }

            @Override
            protected ResolutionResult getResolvedProviderMetadata(final TrustChain chain) {
                assertSame(opChain, chain);
                return opResolution;
            }
        };
        testResolver.setExpiryMargin(1_000L);

        val result = testResolver.resolve(configuration);
        assertSame(expectedMetadata, result.metadata());
        assertSame(expectedJwks, result.federationJWKS());
        assertEquals(List.of("rp-entity-statement"), result.trustChain());
        assertEquals(new Date(rpExpirationTime.getTime() - 1_000L), result.expirationTime());
        assertEquals(List.of(OP_ENTITY_ID, RP_ENTITY_ID), resolvedEntities);
    }

    @Test
    public void testGetResolvedProviderMetadataFailsWhenMetadataClaimIsMissing() {
        val chain = Mockito.mock(TrustChain.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(chain.getLeafConfiguration().getClaimsSet().getMetadata(EntityType.OPENID_PROVIDER)).thenReturn(null);

        val exception = assertThrows(OidcException.class, () -> resolver.getResolvedProviderMetadata(chain));
        assertEquals("No 'metadata' claim in the leaf Entity Statement", exception.getMessage());
    }

    @Test
    public void testGetResolvedProviderMetadataFailsWhenRegistrationTypesAreMissing() {
        val rawMetadataJson = new JSONObject();
        val chain = Mockito.mock(TrustChain.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(chain.getLeafConfiguration().getClaimsSet().getMetadata(EntityType.OPENID_PROVIDER)).thenReturn(rawMetadataJson);

        val exception = assertThrows(OidcException.class, () -> resolver.getResolvedProviderMetadata(chain));
        assertEquals("No 'client_registration_types_supported' claim in the leaf Entity Statement", exception.getMessage());
    }

    @Test
    public void testGetResolvedProviderMetadataFailsWhenFederationJwksIsMissing() {
        val rawMetadataJson = new JSONObject();
        rawMetadataJson.put("client_registration_types_supported", List.of("automatic"));
        val chain = Mockito.mock(TrustChain.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(chain.getLeafConfiguration().getClaimsSet().getMetadata(EntityType.OPENID_PROVIDER)).thenReturn(rawMetadataJson);
        Mockito.when(chain.getLeafConfiguration().getClaimsSet().getJWKSet()).thenReturn(null);

        val exception = assertThrows(OidcException.class, () -> resolver.getResolvedProviderMetadata(chain));
        assertEquals("No federationJWKS found", exception.getMessage());
    }
}
