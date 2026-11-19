package org.pac4j.oidc.client.azuread;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link AzureAdIdTokenValidator}.
 *
 * @author Urmil Trivedi
 * @since 6.7.0
 */
public final class AzureAdIdTokenValidatorTests {

    private static final String SECRET = "123456789012345678901234567890ab";

    private static final String CLIENT_ID = "client-id";

    private static final String MULTI_TENANT_ISSUER = "https://login.microsoftonline.com/%7Btenantid%7D/v2.0";

    private static final String TENANT_A = "11111111-1111-1111-1111-111111111111";

    private static final String TENANT_B = "22222222-2222-2222-2222-222222222222";

    private static AzureAdIdTokenValidator newValidator() {
        val base = new IDTokenValidator(new Issuer(MULTI_TENANT_ISSUER), new ClientID(CLIENT_ID),
            JWSAlgorithm.HS256, new Secret(SECRET));
        return new AzureAdIdTokenValidator(base);
    }

    private static JWT idTokenFor(final String tenantId) throws Exception {
        val now = new Date();
        val claims = new JWTClaimsSet.Builder()
            .issuer("https://login.microsoftonline.com/" + tenantId + "/v2.0")
            .subject("user-of-" + tenantId)
            .audience(CLIENT_ID)
            .issueTime(now)
            .expirationTime(new Date(now.getTime() + 600_000))
            .claim("tid", tenantId)
            .build();
        val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner(SECRET));
        return jwt;
    }

    @Test
    public void testSequentialMultiTenantValidation() throws Exception {
        val validator = newValidator();
        val tokenA = idTokenFor(TENANT_A);
        val tokenB = idTokenFor(TENANT_B);

        for (var i = 0; i < 10; i++) {
            assertEquals("user-of-" + TENANT_A, validator.validate(tokenA, null).getSubject().getValue());
            assertEquals("user-of-" + TENANT_B, validator.validate(tokenB, null).getSubject().getValue());
        }
    }

    @Test
    public void testMissingTidIsRejected() throws Exception {
        val claims = new JWTClaimsSet.Builder()
            .issuer("https://login.microsoftonline.com/" + TENANT_A + "/v2.0")
            .subject("user")
            .audience(CLIENT_ID)
            .issueTime(new Date())
            .expirationTime(new Date(System.currentTimeMillis() + 600_000))
            .build();
        val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner(SECRET));

        assertThrows(BadJWTException.class, () -> newValidator().validate(jwt, null));
    }

    /**
     * The same validator instance is shared by all requests (it is created once by the metadata resolver).
     * Valid ID tokens from different tenants validated concurrently must all be accepted.
     */
    @Test
    public void testConcurrentMultiTenantValidation() throws Exception {
        val validator = newValidator();
        val tokenA = idTokenFor(TENANT_A);
        val tokenB = idTokenFor(TENANT_B);

        val threads = 8;
        val iterations = 2_000;
        val pool = Executors.newFixedThreadPool(threads);
        val start = new CountDownLatch(1);
        val failures = new ConcurrentLinkedQueue<String>();
        final List<Future<?>> futures = new ArrayList<>();
        try {
            for (var t = 0; t < threads; t++) {
                val token = t % 2 == 0 ? tokenA : tokenB;
                futures.add(pool.submit(() -> {
                    start.await();
                    for (var i = 0; i < iterations; i++) {
                        try {
                            validator.validate(token, null);
                        } catch (final Exception e) {
                            failures.add(e.getMessage());
                        }
                    }
                    return null;
                }));
            }
            start.countDown();
            for (val f : futures) {
                f.get(60, TimeUnit.SECONDS);
            }
        } finally {
            shutdown(pool);
        }

        assertTrue(failures.isEmpty(), failures.size() + " of " + (threads * iterations)
            + " valid ID tokens were rejected under concurrency, e.g.: " + failures.peek());
    }

    private static void shutdown(final ExecutorService pool) throws InterruptedException {
        pool.shutdownNow();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
