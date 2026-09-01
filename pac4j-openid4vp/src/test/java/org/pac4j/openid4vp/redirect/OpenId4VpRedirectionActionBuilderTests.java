package org.pac4j.openid4vp.redirect;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.jwt.config.signature.ECSignatureConfiguration;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.WalletInvocationMode;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests the request object actually handed over to a wallet.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpRedirectionActionBuilderTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";
    private static final String CLIENT = "https://app.example.org/callback";
    private static final String DCQL = "{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}";

    private OpenId4VpClient client;
    private OpenId4VpConfiguration configuration;
    private ECSignatureConfiguration signatureConfiguration;

    @BeforeEach
    void setUp() throws Exception {
        val generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        signatureConfiguration = new ECSignatureConfiguration(generator.generateKeyPair());

        configuration = new OpenId4VpConfiguration();
        configuration.setClientId(CLIENT);
        configuration.setClientIdPrefix(ClientIdPrefix.REDIRECT_URI);
        configuration.setDcqlQuery(DCQL);
        configuration.setRequestObjectSignatureConfiguration(signatureConfiguration);
        configuration.addCredentialVerifier(new SdJwtVcVerifier());
        configuration.setInvocationMode(WalletInvocationMode.CUSTOM_SCHEME);

        client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        client.setCallbackUrl(CALLBACK_URL);
    }

    private VpTransaction openTransaction() {
        val ctx = new CallContext(MockWebContext.create(), new MockSessionStore());
        val action = assertInstanceOf(FoundAction.class, client.getRedirectionAction(ctx).get());
        // the wallet is invoked with the custom scheme, carrying the verifier and where to fetch the request
        assertTrue(action.getLocation().startsWith("openid4vp://"));
        assertTrue(action.getLocation().contains(CLIENT_ID + "="));
        assertTrue(action.getLocation().contains(REQUEST_URI + "="));

        val transactionId = ctx.sessionStore().get(ctx.webContext(), SESSION_TRANSACTION_ID).get().toString();
        return configuration.getTransactionStore().get(transactionId).get();
    }

    @Test
    void testTheRequestObjectIsSignedAndTyped() throws Exception {
        val requestObject = SignedJWT.parse(openTransaction().getRequestObject());

        assertEquals(REQUEST_OBJECT_TYPE, requestObject.getHeader().getType().toString());
        assertEquals(JWSAlgorithm.ES256, requestObject.getHeader().getAlgorithm());
        assertTrue(signatureConfiguration.verify(requestObject));
    }

    @Test
    void testTheRequestObjectClaims() throws Exception {
        val transaction = openTransaction();
        val claims = SignedJWT.parse(transaction.getRequestObject()).getJWTClaimsSet();

        assertEquals(ClientIdPrefix.REDIRECT_URI.getValue() + ":" + CLIENT, claims.getStringClaim(CLIENT_ID));
        assertEquals(RESPONSE_TYPE_VP_TOKEN, claims.getStringClaim(RESPONSE_TYPE));
        assertEquals("direct_post.jwt", claims.getStringClaim(RESPONSE_MODE));
        assertEquals(transaction.getNonce(), claims.getStringClaim(NONCE));
        assertNotNull(claims.getExpirationTime());

        // the response comes back on the very endpoint the request object was fetched from
        assertTrue(claims.getStringClaim(RESPONSE_URI).contains(VP_TRANSACTION_ID + "=" + transaction.getId()));

        assertEquals(List.of(Map.of("id", "pid", "format", "dc+sd-jwt")),
            claims.getJSONObjectClaim(DCQL_QUERY).get("credentials"));
    }

    @Test
    void testThePublishedEncryptionKeyIsPublicOnly() throws Exception {
        val transaction = openTransaction();
        val claims = SignedJWT.parse(transaction.getRequestObject()).getJWTClaimsSet();

        // the private part is kept in the transaction, only the public one reaches the wallet
        assertTrue(ECKey.parse(transaction.getEncryptionKey()).isPrivate());

        val jwks = (Map<String, Object>) claims.getJSONObjectClaim(CLIENT_METADATA).get(JWKS);
        val keys = (List<Map<String, Object>>) jwks.get(KEYS);
        assertEquals(1, keys.size());
        assertFalse(ECKey.parse(keys.get(0)).isPrivate());
    }

    @Test
    void testEachTransactionHasItsOwnKeyAndNonce() {
        val first = openTransaction();
        val second = openTransaction();

        assertTrue(!first.getNonce().equals(second.getNonce()));
        assertTrue(!first.getEncryptionKey().equals(second.getEncryptionKey()));
    }
}
