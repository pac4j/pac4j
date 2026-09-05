package org.pac4j.openid4vp.request;

import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.openid4vp.client.OpenId4VpDcApiClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpDcApiConfiguration;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.test.util.TestsHelper;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests the request object handed to the digital credentials API: the same signed object as the one a wallet
 * fetches by URL, save for the claims tied to the way the wallet is reached.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class DcApiRequestObjectBuilderTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";
    private static final String ORIGIN = "https://app.example.org";

    @TempDir
    private Path directory;

    private OpenId4VpDcApiConfiguration configuration;
    private OpenId4VpDcApiClient client;

    @BeforeEach
    void setUp() {
        configuration = new OpenId4VpDcApiConfiguration();
        configuration.setClientId("app.example.org")
            .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.setExpectedOrigins(List.of(ORIGIN));
        configuration.addCredentialVerifier(new SdJwtVcVerifier());

        client = new OpenId4VpDcApiClient(configuration);
        client.setName("Wallet");
        client.setCallbackUrl(CALLBACK_URL);
    }

    private SignedJWT handOver() throws Exception {
        val ctx = new CallContext(MockWebContext.create(), new MockSessionStore());
        val action = assertInstanceOf(OkAction.class, client.getRedirectionAction(ctx).get());

        // the page receives the JSON the API expects, the signed request object in its "request" member
        val content = action.getContent();
        assertTrue(content.startsWith("{\"" + REQUEST + "\":\""));
        return SignedJWT.parse(content.substring(content.indexOf(':') + 2, content.length() - 2));
    }

    @Test
    void testTheRequestIsTheSameSignedObject() throws Exception {
        val requestObject = handOver();

        assertEquals(REQUEST_OBJECT_TYPE, requestObject.getHeader().getType().toString());
        val claims = requestObject.getJWTClaimsSet();
        assertEquals(RESPONSE_TYPE_VP_TOKEN, claims.getStringClaim(RESPONSE_TYPE));
        assertNotNull(claims.getStringClaim(NONCE));
        assertNotNull(claims.getJSONObjectClaim(DCQL_QUERY));
        assertNotNull(claims.getJSONObjectClaim(CLIENT_METADATA));
    }

    @Test
    void testTheClaimsTiedToThisBinding() throws Exception {
        val claims = handOver().getJWTClaimsSet();

        // the answer comes back through the browser, so there is nowhere to post it
        assertNull(claims.getStringClaim(RESPONSE_URI));
        assertEquals("dc_api.jwt", claims.getStringClaim(RESPONSE_MODE));
        assertEquals(List.of(ORIGIN), claims.getStringListClaim(EXPECTED_ORIGINS));
        // a signed request must tell the wallet who is asking
        assertEquals(ClientIdPrefix.DECENTRALIZED_IDENTIFIER.getValue() + ":app.example.org", claims.getStringClaim(CLIENT_ID));
    }

    @Test
    void testTheExpectedOriginsAreMandatory() {
        configuration.setExpectedOrigins(List.of());

        TestsHelper.expectException(client::init, TechnicalException.class,
            "expectedOrigins cannot be empty: a signed request sent over the digital credentials API must tell the wallet "
                + "which origins it may come from");
    }

    @Test
    void testAnUnsafeOriginIsRefused() {
        configuration.setExpectedOrigins(List.of("javascript:alert(1)"));

        TestsHelper.expectException(client::init, TechnicalException.class,
            "unsafe scheme for an expected origin: javascript:alert(1)");
    }
}
