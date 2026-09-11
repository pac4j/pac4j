package org.pac4j.openid4vp.request;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.FoundAction;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.verifier.CredentialVerifier;
import org.pac4j.openid4vp.verifier.VerifiedCredential;
import org.pac4j.openid4vp.wallet.WalletSimulator;
import org.pac4j.openid4vp.dcql.DcqlQuery;
import org.pac4j.openid4vp.config.VerifierAttestation;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests the request object actually handed over to a wallet, built when the wallet asks for it.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpRequestObjectBuilderTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";
    private static final String CLIENT = "https://app.example.org/callback";
    private static final String DCQL = "{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}";

    @TempDir
    private java.nio.file.Path directory;

    private OpenId4VpClient client;
    private OpenId4VpConfiguration configuration;

    @BeforeEach
    void setUp() throws Exception {
        configuration = new OpenId4VpConfiguration();
        configuration.setJwks(new JwksProperties()
            .setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.setClientId(CLIENT);
        configuration.setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setDcqlQuery(DCQL);

        client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        client.setCallbackUrl(CALLBACK_URL);
    }

    /** Open a transaction, then build its request object as the wallet fetching it would. */
    private SignedJWT requestObjectOf(final VpTransaction transaction) throws Exception {
        return SignedJWT.parse(client.getRequestObjectBuilder()
            .build(new CallContext(MockWebContext.create(), new MockSessionStore()), transaction));
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
        val requestObject = requestObjectOf(openTransaction());

        assertEquals(REQUEST_OBJECT_TYPE, requestObject.getHeader().getType().toString());
        assertEquals(JWSAlgorithm.ES256, requestObject.getHeader().getAlgorithm());
        assertTrue(requestObject.verify(
            new ECDSAVerifier(((ECKey) configuration.getRequestObjectSigningKey()).toPublicJWK())));
    }

    @Test
    void testTheRequestObjectClaims() throws Exception {
        val transaction = openTransaction();
        val claims = requestObjectOf(transaction).getJWTClaimsSet();

        assertEquals(ClientIdPrefix.DECENTRALIZED_IDENTIFIER.getValue() + ":" + CLIENT, claims.getStringClaim(CLIENT_ID));
        assertEquals(RESPONSE_TYPE_VP_TOKEN, claims.getStringClaim(RESPONSE_TYPE));
        assertEquals("direct_post.jwt", claims.getStringClaim(RESPONSE_MODE));
        assertEquals(transaction.getNonce(), claims.getStringClaim(NONCE));
        assertNotNull(claims.getExpirationTime());
        // no wallet is discovered beforehand: the symbolic audience of the static discovery case
        assertEquals(List.of(REQUEST_OBJECT_AUDIENCE), claims.getAudience());

        // the response comes back on the very endpoint the request object was fetched from
        assertTrue(claims.getStringClaim(RESPONSE_URI).contains(VP_TRANSACTION_ID + "=" + transaction.getId()));

        // the meta member is always sent, empty when there is no constraint
        assertEquals(List.of(Map.of("id", "pid", "format", "dc+sd-jwt", "meta", Map.of())),
            claims.getJSONObjectClaim(DCQL_QUERY).get("credentials"));
    }

    @Test
    void testAScopeStandsForTheDcqlQuery() throws Exception {
        configuration.setDcqlQuery((DcqlQuery) null).setScope("com.example.pid_presentation");
        val transaction = openTransaction();
        val claims = requestObjectOf(transaction).getJWTClaimsSet();

        // "Either a dcql_query or a scope parameter representing a DCQL Query MUST be present [...], but not both"
        assertEquals("com.example.pid_presentation", claims.getStringClaim(SCOPE));
        assertNull(claims.getClaim(DCQL_QUERY));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTheVerifierAttestationsAreSentAsIs() throws Exception {
        configuration.getVerifierInfo().add(new VerifierAttestation("jwt", "eyJhbGciOiJFUzI1NiJ9.registration.certificate")
            .setCredentialIds(List.of("pid")));
        configuration.getVerifierInfo().add(new VerifierAttestation("example+json", Map.of("purpose", "age verification")));
        val claims = requestObjectOf(openTransaction()).getJWTClaimsSet();

        val verifierInfo = (List<Map<String, Object>>) claims.getClaim(VERIFIER_INFO);
        assertEquals(2, verifierInfo.size());
        assertEquals(Map.of(FORMAT, "jwt", DATA, "eyJhbGciOiJFUzI1NiJ9.registration.certificate", CREDENTIAL_IDS, List.of("pid")),
            verifierInfo.get(0));
        // relevant to every requested credential: no credential_ids
        assertEquals(Map.of(FORMAT, "example+json", DATA, Map.of("purpose", "age verification")), verifierInfo.get(1));
    }

    @Test
    void testNoVerifierInfoParameterWithoutAttestations() throws Exception {
        val claims = requestObjectOf(openTransaction()).getJWTClaimsSet();
        assertNull(claims.getClaim(VERIFIER_INFO));
    }

    /** A second registered format, so that the narrowing down to what the wallet declares is visible. */
    private void registerAnMdocVerifier() {
        configuration.addCredentialVerifier(new CredentialVerifier() {
            @Override
            public CredentialFormat getFormat() {
                return CredentialFormat.MSO_MDOC;
            }

            @Override
            public VerifiedCredential verify(final String rawCredential, final VpTransaction transaction,
                                             final OpenId4VpConfiguration configuration) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test
    void testABlankScopeIsNoScope() throws Exception {
        // what a properties file binding hands over for an absent scope: accepted at initialization, so the
        // DCQL query must go out, not an empty scope
        configuration.setScope("  ");
        val transaction = openTransaction();
        val claims = requestObjectOf(transaction).getJWTClaimsSet();

        assertNull(claims.getClaim(SCOPE));
        assertNotNull(claims.getJSONObjectClaim(DCQL_QUERY));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTheFormatsAreNarrowedDownToWhatTheWalletDeclares() throws Exception {
        registerAnMdocVerifier();
        val transaction = openTransaction();
        // fetched with a GET, the wallet said nothing: every verified format is published
        var formats = (Map<String, Object>) requestObjectOf(transaction).getJWTClaimsSet()
            .getJSONObjectClaim(CLIENT_METADATA).get(VP_FORMATS_SUPPORTED);
        assertEquals(List.of("dc+sd-jwt", "mso_mdoc"), List.copyOf(formats.keySet()));

        // posted with its metadata, the wallet only presents SD-JWT VCs: the mobile documents are left out
        transaction.setWalletMetadata(WalletSimulator.WALLET_METADATA_JSON);
        formats = (Map<String, Object>) requestObjectOf(transaction).getJWTClaimsSet()
            .getJSONObjectClaim(CLIENT_METADATA).get(VP_FORMATS_SUPPORTED);
        assertEquals(List.of("dc+sd-jwt"), List.copyOf(formats.keySet()));
    }

    @Test
    void testAWalletPresentingNoneOfTheVerifiedFormatsIsRefused() {
        val transaction = openTransaction()
            .setWalletMetadata("{\"vp_formats_supported\":{\"mso_mdoc\":{}}}");

        val e = assertThrows(OpenId4VpException.class, () -> requestObjectOf(transaction));
        assertEquals("the wallet presents none of the credential formats this verifier verifies [dc+sd-jwt], "
            + "but [mso_mdoc]: " + transaction.getId(), e.getMessage());
    }

    @Test
    void testTheEncryptionIsNarrowedDownToWhatTheWalletDeclares() throws Exception {
        val transaction = openTransaction()
            .setWalletMetadata("{\"vp_formats_supported\":{\"dc+sd-jwt\":{}},"
                + "\"authorization_encryption_enc_values_supported\":[\"A256GCM\",\"A128CBC-HS256\"]}");

        val metadata = requestObjectOf(transaction).getJWTClaimsSet().getJSONObjectClaim(CLIENT_METADATA);
        assertEquals(List.of("A256GCM"), metadata.get(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED));
    }

    @Test
    void testAWalletEncryptingWithNoneOfTheAcceptedAlgorithmsIsRefused() {
        val transaction = openTransaction()
            .setWalletMetadata("{\"vp_formats_supported\":{\"dc+sd-jwt\":{}},"
                + "\"authorization_encryption_enc_values_supported\":[\"A128CBC-HS256\"]}");

        val e = assertThrows(OpenId4VpException.class, () -> requestObjectOf(transaction));
        assertEquals("the wallet encrypts its response with none of the accepted content encryption algorithms "
            + "[A128GCM, A256GCM], but with [A128CBC-HS256]: " + transaction.getId(), e.getMessage());
    }

    @Test
    void testThePublishedEncryptionKeyIsPublicOnly() throws Exception {
        val transaction = openTransaction();
        val claims = requestObjectOf(transaction).getJWTClaimsSet();

        // the private part is kept in the transaction, only the public one reaches the wallet
        assertTrue(ECKey.parse(transaction.getEncryptionKey()).isPrivate());

        val jwks = (Map<String, Object>) claims.getJSONObjectClaim(CLIENT_METADATA).get(JWKS);
        val keys = (List<Map<String, Object>>) jwks.get(KEYS);
        assertEquals(1, keys.size());
        assertFalse(ECKey.parse(keys.get(0)).isPrivate());
        // ECDH-ES on P-256, and both content encryptions the high assurance profile requires a verifier to list
        assertEquals("ECDH-ES", keys.get(0).get("alg"));
        assertEquals("P-256", keys.get(0).get("crv"));
        assertEquals(List.of("A128GCM", "A256GCM"),
            claims.getJSONObjectClaim(CLIENT_METADATA).get(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED));
    }

}
