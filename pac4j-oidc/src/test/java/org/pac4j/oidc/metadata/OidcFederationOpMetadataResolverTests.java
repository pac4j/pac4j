package org.pac4j.oidc.metadata;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.federation.entity.EntityConfigurationGenerator;
import org.pac4j.oidc.metadata.chain.FederationChainResolver;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.pac4j.test.web.ServerResponse;
import org.pac4j.test.web.WebServer;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link OidcFederationOpMetadataResolver}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class OidcFederationOpMetadataResolverTests {

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
    private OidcConfiguration configuration;
    private TokenValidator mockedTokenValidator;

    @TempDir
    Path tmp;

    @BeforeEach
    public void beforeEach() {
        configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        mockedTokenValidator = Mockito.mock(TokenValidator.class);
    }

    @Test
    public void testInitWiresTokenValidatorAndClientAuthenticationForTokenAndParEndpoints() throws Exception {

        val expectedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        expectedMetadata.setPushedAuthorizationRequestEndpointURI(new URI("https://op.example.org/par"));

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected FederationChainResolver.ResolutionResult resolveMetadata() {
                return new FederationChainResolver.ResolutionResult(expectedMetadata,
                    new Date(System.currentTimeMillis() + 60_000), new JWKSet(), null);
            }

            @Override
            protected TokenValidator createTokenValidator() {
                // Avoid triggering OidcConfiguration#init() through TokenValidator RSA validators.
                return mockedTokenValidator;
            }
        };

        val loaded = resolver.load();
        assertSame(expectedMetadata, loaded);
        assertSame(mockedTokenValidator, resolver.getTokenValidator());

        val tokenEndpointAuth = resolver.getClientAuthenticationTokenEndpoint();
        assertNotNull(tokenEndpointAuth);
        assertTrue(tokenEndpointAuth instanceof ClientSecretBasic);

        val parEndpointAuth = resolver.getClientAuthenticationPAREndpoint();
        assertNotNull(parEndpointAuth);
        assertTrue(parEndpointAuth instanceof ClientSecretBasic);
    }

    @Test
    public void testExplicitRegistrationSetsClientId() throws Exception {
        val secretFile = tmp.resolve("federation-secret-resolver-test.txt");
        val explicitConfiguration = new OidcConfiguration();
        explicitConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        explicitConfiguration.getFederation().setEntityId("https://rp.example.org");
        explicitConfiguration.getFederation().setSecretExportFile(secretFile.toString());

        val entityConfigurationGenerator = Mockito.mock(EntityConfigurationGenerator.class);
        Mockito.when(entityConfigurationGenerator.getContentType()).thenReturn("application/entity-statement+jwt");
        Mockito.when(entityConfigurationGenerator.generateEntityStatement()).thenReturn("entity-configuration");
        explicitConfiguration.getFederation().setEntityConfigurationGenerator(entityConfigurationGenerator);

        val signingKey = new RSAKeyGenerator(2048).keyID("registration-key").generate();
        val registrationResponse = buildRegistrationResponseJwt(signingKey, "registeredClient", "registeredSecret");

        val webServer = new WebServer(0)
            .defineResponse("ok", new ServerResponse(WebServer.Response.Status.CREATED,
                "application/entity-statement+jwt", registrationResponse));
        webServer.start();
        try {
            val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
            metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT));
            metadata.setFederationRegistrationEndpointURI(
                new URI("http://localhost:" + webServer.getListeningPort() + "/register?r=ok"));
            val jwks = new JWKSet(signingKey.toPublicJWK());

            val resolver = new OidcFederationOpMetadataResolver(explicitConfiguration) {
                @Override
                protected FederationChainResolver.ResolutionResult resolveMetadata() {
                    return new FederationChainResolver.ResolutionResult(metadata,
                        new Date(System.currentTimeMillis() + 60_000), jwks, null);
                }

                @Override
                protected TokenValidator createTokenValidator() {
                    return mockedTokenValidator;
                }
            };

            assertSame(metadata, resolver.load());
            assertEquals("registeredClient", explicitConfiguration.getClientId());
            assertTrue(Files.exists(secretFile));
            assertEquals("registeredSecret", Files.readString(secretFile));
            assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, resolver.getClientAuthenticationTokenEndpoint().getMethod());
            assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, resolver.getClientAuthenticationPAREndpoint().getMethod());

            Mockito.verify(entityConfigurationGenerator).generateEntityStatement();
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testReloadsMetadataInBackgroundWhenTrustChainExpired() throws Exception {
        val initialMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val refreshedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val metadataRetrievalCount = new AtomicInteger(0);
        val backgroundReloadStarted = new CountDownLatch(1);
        val allowBackgroundReloadCompletion = new CountDownLatch(1);

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected FederationChainResolver.ResolutionResult resolveMetadata() {
                if (metadataRetrievalCount.getAndIncrement() == 0) {
                    return new FederationChainResolver.ResolutionResult(initialMetadata,
                        new Date(System.currentTimeMillis() + 60_000), new JWKSet(), null);
                }
                backgroundReloadStarted.countDown();
                try {
                    if (!allowBackgroundReloadCompletion.await(2, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timeout waiting for background reload completion signal");
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                }
                return new FederationChainResolver.ResolutionResult(refreshedMetadata,
                    new Date(System.currentTimeMillis() + 120_000), new JWKSet(), null);
            }

            @Override
            protected TokenValidator createTokenValidator() {
                return mockedTokenValidator;
            }
        };
        assertSame(initialMetadata, resolver.load());
        assertEquals(1, metadataRetrievalCount.get());
        resolver.setChainExpirationTime(new Date(System.currentTimeMillis() - 1_000));

        val start = System.nanoTime();
        assertSame(initialMetadata, resolver.load());
        val elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsedMs < 500, "Expected non-blocking load while background reload is running");
        assertTrue(backgroundReloadStarted.await(1, TimeUnit.SECONDS));
        assertEquals(2, metadataRetrievalCount.get());

        resolver.load();
        resolver.load();
        assertEquals(2, metadataRetrievalCount.get());

        allowBackgroundReloadCompletion.countDown();

        val timeoutAt = System.currentTimeMillis() + 2_000;
        var loaded = resolver.load();
        while (loaded != refreshedMetadata && System.currentTimeMillis() < timeoutAt) {
            Thread.sleep(20);
            loaded = resolver.load();
        }
        assertSame(refreshedMetadata, loaded);
    }

    @Test
    public void testConcurrentCallsDoNotStartMultipleBackgroundReloads() throws Exception {

        val initialMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val refreshedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val metadataRetrievalCount = new AtomicInteger(0);
        val backgroundReloadStarted = new CountDownLatch(1);
        val allowBackgroundReloadCompletion = new CountDownLatch(1);

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected FederationChainResolver.ResolutionResult resolveMetadata() {
                if (metadataRetrievalCount.getAndIncrement() == 0) {
                    return new FederationChainResolver.ResolutionResult(initialMetadata,
                        new Date(System.currentTimeMillis() + 60_000), new JWKSet(), null);
                }
                backgroundReloadStarted.countDown();
                try {
                    if (!allowBackgroundReloadCompletion.await(2, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timeout waiting for background reload completion signal");
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                }
                return new FederationChainResolver.ResolutionResult(refreshedMetadata,
                    new Date(System.currentTimeMillis() + 120_000), new JWKSet(), null);
            }

            @Override
            protected TokenValidator createTokenValidator() {
                return mockedTokenValidator;
            }
        };

        assertSame(initialMetadata, resolver.load());
        assertEquals(1, metadataRetrievalCount.get());

        resolver.setChainExpirationTime(new Date(System.currentTimeMillis() - 1_000));

        val pool = Executors.newFixedThreadPool(6);
        try {
            val ready = new CountDownLatch(6);
            val fire = new CountDownLatch(1);
            val futures = new ArrayList<Future<OIDCProviderMetadata>>();
            for (int i = 0; i < 6; i++) {
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    if (!fire.await(1, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timeout waiting to start concurrent reload calls");
                    }
                    return resolver.load();
                }));
            }

            assertTrue(ready.await(1, TimeUnit.SECONDS));
            fire.countDown();
            assertTrue(backgroundReloadStarted.await(1, TimeUnit.SECONDS));

            for (val future : futures) {
                assertSame(initialMetadata, future.get(1, TimeUnit.SECONDS));
            }

            assertEquals(2, metadataRetrievalCount.get());

            allowBackgroundReloadCompletion.countDown();

            val timeoutAt = System.currentTimeMillis() + 2_000;
            var loaded = resolver.load();
            while (loaded != refreshedMetadata && System.currentTimeMillis() < timeoutAt) {
                Thread.sleep(20);
                loaded = resolver.load();
            }
            assertSame(refreshedMetadata, loaded);
        } finally {
            pool.shutdownNow();
        }
    }

    private static String buildRegistrationResponseJwt(final JWK signingKey,
                                                       final String clientId,
                                                       final String clientSecret) {
        val now = new Date();
        val claims = new JWTClaimsSet.Builder()
            .issuer("https://op.example.org")
            .subject("https://op.example.org")
            .audience("https://rp.example.org")
            .issueTime(now)
            .expirationTime(new Date(now.getTime() + 60_000))
            .claim("jwks", new JWKSet(signingKey.toPublicJWK()).toJSONObject())
            .claim("metadata", Map.of("openid_relying_party", Map.of(
                "client_id", clientId,
                "client_secret", clientSecret)))
            .build();
        return JwkHelper.buildSignedJwt(claims, signingKey, "entity-statement+jwt");
    }
}
