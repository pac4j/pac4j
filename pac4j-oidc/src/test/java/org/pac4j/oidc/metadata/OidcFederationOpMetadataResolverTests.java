package org.pac4j.oidc.metadata;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.TokenValidator;

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


    @Test
    public void testInitWiresTokenValidatorAndClientAuthentication() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        val expectedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected FederationChainResolver.ResolutionResult resolveMetadata() {
                return new FederationChainResolver.ResolutionResult(expectedMetadata, new Date(System.currentTimeMillis() + 60_000));
            }

            @Override
            protected TokenValidator createTokenValidator() {
                // Avoid triggering OidcConfiguration#init() through TokenValidator RSA validators.
                return Mockito.mock(TokenValidator.class);
            }
        };

        val loaded = resolver.load();
        assertSame(expectedMetadata, loaded);

        assertNotNull(resolver.getTokenValidator());

        val auth = resolver.getClientAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof ClientSecretBasic);
    }

    @Test
    public void testReloadsMetadataInBackgroundWhenTrustChainExpired() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        val initialMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val refreshedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val metadataRetrievalCount = new AtomicInteger(0);
        val backgroundReloadStarted = new CountDownLatch(1);
        val allowBackgroundReloadCompletion = new CountDownLatch(1);

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected FederationChainResolver.ResolutionResult resolveMetadata() {
                if (metadataRetrievalCount.getAndIncrement() == 0) {
                    return new FederationChainResolver.ResolutionResult(initialMetadata, new Date(System.currentTimeMillis() + 60_000));
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
                return new FederationChainResolver.ResolutionResult(refreshedMetadata, new Date(System.currentTimeMillis() + 120_000));
            }

            @Override
            protected TokenValidator createTokenValidator() {
                return Mockito.mock(TokenValidator.class);
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
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        val initialMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val refreshedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        val metadataRetrievalCount = new AtomicInteger(0);
        val backgroundReloadStarted = new CountDownLatch(1);
        val allowBackgroundReloadCompletion = new CountDownLatch(1);

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected FederationChainResolver.ResolutionResult resolveMetadata() {
                if (metadataRetrievalCount.getAndIncrement() == 0) {
                    return new FederationChainResolver.ResolutionResult(initialMetadata, new Date(System.currentTimeMillis() + 60_000));
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
                return new FederationChainResolver.ResolutionResult(refreshedMetadata, new Date(System.currentTimeMillis() + 120_000));
            }

            @Override
            protected TokenValidator createTokenValidator() {
                return Mockito.mock(TokenValidator.class);
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
                    fire.await(1, TimeUnit.SECONDS);
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
}
