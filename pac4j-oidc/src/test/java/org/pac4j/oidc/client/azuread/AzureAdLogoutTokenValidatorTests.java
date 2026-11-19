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
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link AzureAdLogoutTokenValidator}.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public final class AzureAdLogoutTokenValidatorTests {

    private static final String SECRET = "123456789012345678901234567890ab";

    private static final String CLIENT_ID = "client-id";

    private static final String MULTI_TENANT_ISSUER = "https://login.microsoftonline.com/%7Btenantid%7D/v2.0";

    private static final String TENANT_A = "11111111-1111-1111-1111-111111111111";

    private static final String TENANT_B = "22222222-2222-2222-2222-222222222222";

    private static AzureAdLogoutTokenValidator newValidator() {
        val base = new LogoutTokenValidator(new Issuer(MULTI_TENANT_ISSUER), new ClientID(CLIENT_ID),
            JWSAlgorithm.HS256, new Secret(SECRET));
        return new AzureAdLogoutTokenValidator(base);
    }

    // the claims of a logout token: OpenID Connect Back-Channel Logout 1.0, section 2.4
    private static JWT logoutTokenFor(final String tenantId, final boolean withTid) throws Exception {
        val now = new Date();
        val claims = new JWTClaimsSet.Builder()
            .issuer("https://login.microsoftonline.com/" + tenantId + "/v2.0")
            .subject("user-of-" + tenantId)
            .audience(CLIENT_ID)
            .issueTime(now)
            .expirationTime(new Date(now.getTime() + 600_000))
            .jwtID(UUID.randomUUID().toString())
            .claim("events", Map.of("http://schemas.openid.net/event/backchannel-logout", Map.of()));
        if (withTid) {
            claims.claim("tid", tenantId);
        }
        val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
        jwt.sign(new MACSigner(SECRET));
        return jwt;
    }

    @Test
    public void testSequentialMultiTenantValidation() throws Exception {
        val validator = newValidator();
        val tokenA = logoutTokenFor(TENANT_A, true);
        val tokenB = logoutTokenFor(TENANT_B, true);

        for (var i = 0; i < 10; i++) {
            assertEquals("user-of-" + TENANT_A, validator.validate(tokenA).getSubject().getValue());
            assertEquals("user-of-" + TENANT_B, validator.validate(tokenB).getSubject().getValue());
        }
    }

    @Test
    public void testMissingTidIsRejected() throws Exception {
        val jwt = logoutTokenFor(TENANT_A, false);

        assertThrows(BadJWTException.class, () -> newValidator().validate(jwt));
    }

    /**
     * The same validator instance is shared by all requests (it is created once by the metadata resolver).
     * Valid logout tokens from different tenants validated concurrently must all be accepted.
     */
    @Test
    public void testConcurrentMultiTenantValidation() throws Exception {
        val validator = newValidator();
        val tokenA = logoutTokenFor(TENANT_A, true);
        val tokenB = logoutTokenFor(TENANT_B, true);

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
                            validator.validate(token);
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
            pool.shutdownNow();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }

        assertTrue(failures.isEmpty(), failures.size() + " of " + (threads * iterations)
            + " valid logout tokens were rejected under concurrency, e.g.: " + failures.peek());
    }
}
