package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.generator.RandomValueGenerator;
import org.pac4j.core.util.generator.ValueGenerator;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertUtils;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import lombok.AccessLevel;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.util.Announcement;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.transaction.VpTransactionStore;
import org.pac4j.openid4vp.verifier.CredentialVerifier;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateParsingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * The configuration of an OpenID4VP verifier (relying party).
 *
 * <p>The defaults are the ones mandated by the high assurance interoperability profile (HAIP), which the
 * EUDI architecture and reference framework relies on: a signed request object served by reference, an
 * encrypted response posted directly, and ES256 as the signature algorithm.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OpenId4VpConfiguration extends BaseClientConfiguration {

    private static final Announcement ANNOUNCE_CLEAR_RESPONSE =
        new Announcement("An unencrypted response mode is used: the credentials will travel in clear, readable by whatever "
            + "carries them before they reach this application, be it a TLS termination and its logs or the page itself. The "
            + "high assurance profile mandates an encrypted response mode, and so does the EUDI wallet");

    private static final Announcement ANNOUNCE_UNSIGNED_REQUEST =
        new Announcement("An unsigned request is used: the wallet has no way to authenticate this verifier, the End-User "
            + "only sees where the answer will go, and the request must not be shown as a QR code, where nothing tells "
            + "the End-User who is asking. The high assurance profile mandates a signed request, and so does the EUDI "
            + "wallet: keep this prefix for development");

    /** The default lifetime of a presentation request, in seconds. */
    public static final int DEFAULT_TRANSACTION_LIFETIME_SECONDS = 300;

    /** The signature algorithm mandated by the high assurance profile, and the curve it implies. */
    public static final JWSAlgorithm DEFAULT_SIGNING_ALGORITHM = JWSAlgorithm.ES256;

    /** The identifier of this verifier, without its prefix: a DNS name for {@link ClientIdPrefix#X509_SAN_DNS}. */
    private String clientId;

    private ClientIdPrefix clientIdPrefix = ClientIdPrefix.X509_SAN_DNS;

    private ResponseMode responseMode = ResponseMode.DIRECT_POST_JWT;

    /** The credential formats requested from the wallet. */
    private List<CredentialFormat> supportedFormats = new ArrayList<>(List.of(CredentialFormat.SD_JWT_VC));

    /**
     * The DCQL query, as a raw JSON string for now. Presentation exchange is deliberately not supported:
     * it is out of the HAIP profile and no longer used by the EUDI ecosystem.
     */
    private String dcqlQuery;

    /** Where the signing key comes from: a JWKS, or a keystore when it is not defined. */
    private JwksProperties jwks = new JwksProperties();

    private KeystoreProperties keystore = new KeystoreProperties();

    /**
     * Signs the request object, resolved from the sources above at initialization. The signature algorithm is
     * derived from the key itself, so a P-256 key gives the ES256 the profile mandates and there is no second
     * setting to keep in agreement with it.
     */
    @Setter(AccessLevel.NONE)
    private JWK requestObjectSigningKey;

    /** The verifiers, by credential format. */
    private Map<CredentialFormat, CredentialVerifier> credentialVerifiers = new LinkedHashMap<>();

    /**
     * How long a presentation request stays valid. It is stamped on each transaction, sent to the wallet in
     * the request object, and the store drops the transaction on that very date.
     */
    private int transactionLifetimeSeconds = DEFAULT_TRANSACTION_LIFETIME_SECONDS;

    private Store<String, VpTransaction> transactionStore = new VpTransactionStore();

    private ValueGenerator nonceGenerator = new RandomValueGenerator(32);

    private ValueGenerator transactionIdGenerator = new RandomValueGenerator(32);

    /** The custom scheme used to invoke a wallet on the same device. */
    private String walletScheme = "openid4vp://";

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("clientIdPrefix", clientIdPrefix);
        assertNotNull("responseMode", responseMode);
        assertNotNull("transactionStore", transactionStore);
        assertNotNull("nonceGenerator", nonceGenerator);
        assertNotNull("transactionIdGenerator", transactionIdGenerator);
        assertNotBlank("dcqlQuery", dcqlQuery);
        try {
            JSONObjectUtils.parse(dcqlQuery);
        } catch (final ParseException e) {
            throw new TechnicalException("dcqlQuery is not a JSON object: " + e.getMessage(), e);
        }
        assertTrue(transactionLifetimeSeconds > 0, "transactionLifetimeSeconds must be greater than zero");
        assertTrue(supportedFormats != null && !supportedFormats.isEmpty(), "supportedFormats cannot be empty");
        checkResponseModeBinding();
        if (!responseMode.isEncrypted()) {
            ANNOUNCE_CLEAR_RESPONSE.announce();
        }

        if (clientIdPrefix.isSignedRequest()) {
            requestObjectSigningKey = JwkHelper.resolveSigningKey(jwks, keystore, DEFAULT_SIGNING_ALGORITHM);
        } else {
            // OpenID4VP 1.0, the redirect_uri prefix: "Requests using the redirect_uri Client Identifier Prefix cannot be
            // signed because there is no method for the Wallet to obtain a trusted key for verification"
            // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes
            ANNOUNCE_UNSIGNED_REQUEST.announce();
        }
        if (publishesCertificateChain()) {
            assertTrue(requestObjectSigningKey.getX509CertChain() != null && !requestObjectSigningKey.getX509CertChain().isEmpty(),
                "the signing key must carry a certificate chain for the " + clientIdPrefix.getValue()
                    + " client identifier prefix: load it from a keystore, or from a JWKS holding a x5c member");
        }
        if (publishesCertificateChain()) {
            requestObjectSigningKey = withoutTrustAnchor(requestObjectSigningKey);
        }
        if (clientIdPrefix == ClientIdPrefix.X509_HASH) {
            // the identifier is the certificate itself: computed rather than typed, so that it cannot diverge
            clientId = computeCertificateHash();
        }
        if (clientIdPrefix == ClientIdPrefix.DECENTRALIZED_IDENTIFIER) {
            // OpenID4VP 1.0, the decentralized_identifier prefix: "a particular public key used to sign the request
            // in question MUST be identified by the kid in the JOSE Header"
            // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes
            assertTrue(requestObjectSigningKey.getKeyID() != null,
                "the signing key must carry a key identifier for the decentralized_identifier client identifier prefix: "
                    + "the wallet looks the key up in the DID document by the kid of the request object");
        }
        if (clientIdPrefix == ClientIdPrefix.REDIRECT_URI) {
            // the identifier is the response URI of each transaction: derived there, never typed here
            clientId = null;
        } else {
            assertNotBlank("clientId", clientId);
        }
        if (clientIdPrefix == ClientIdPrefix.X509_SAN_DNS) {
            checkClientIdIsASubjectAlternativeName();
        }
        supportedFormats.forEach(format -> assertNotNull("credentialVerifier for " + format.getValue(),
            credentialVerifiers.get(format)));
    }

    /**
     * <p>Refuse a response mode which belongs to the other binding: a wallet invoked by a URL posts its
     * answer, it cannot return it through the digital credentials API.</p>
     */
    protected void checkResponseModeBinding() {
        assertTrue(!responseMode.isOverDcApi(), "the " + responseMode.getValue() + " response mode belongs to the digital "
            + "credentials API: a wallet invoked by a URL posts its answer, use " + ResponseMode.DIRECT_POST_JWT.getValue());
    }

    /**
     * <p>For the {@code x509_san_dns} prefix, the client identifier must be a DNS name found among the
     * subject alternative names of the leaf certificate, otherwise the wallet refuses the request.</p>
     *
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes">
 *     OpenID4VP 1.0, the x509_san_dns prefix</a>
     */
    protected void checkClientIdIsASubjectAlternativeName() {
        try {
            val leaf = X509CertUtils.parse(requestObjectSigningKey.getX509CertChain().get(0).decode());
            val names = leaf.getSubjectAlternativeNames();
            val dnsNames = new ArrayList<String>();
            if (names != null) {
                for (val name : names) {
                    // a general name is a pair: its type, then its value; 2 is dNSName
                    if (Integer.valueOf(2).equals(name.get(0))) {
                        dnsNames.add(String.valueOf(name.get(1)));
                    }
                }
            }
            assertTrue(dnsNames.stream().anyMatch(clientId::equalsIgnoreCase),
                "the client identifier '" + clientId + "' must be a dNSName subject alternative name of the leaf "
                    + "certificate for the x509_san_dns client identifier prefix, but it holds: " + dnsNames);
        } catch (final CertificateParsingException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * <p>The signing key with the trust anchor removed from its certificate chain, when the chain ends with
     * one: HAIP has it that "The X.509 certificate of the trust anchor MUST NOT be included in the x5c JOSE
     * header of the request object". A keystore hands the chain over whole, root included, so it is trimmed
     * here once and for all, the leaf and the intermediates staying in place.</p>
     *
     * @param key the signing key as loaded
     * @return the key to sign and publish with
     * @see <a href="https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html">HAIP</a>
     */
    protected JWK withoutTrustAnchor(final JWK key) {
        val chain = key.getX509CertChain();
        if (chain == null || chain.size() < 2) {
            return key;
        }
        val last = X509CertUtils.parse(chain.get(chain.size() - 1).decode());
        if (last == null || !last.getSubjectX500Principal().equals(last.getIssuerX500Principal())) {
            return key;
        }
        val trimmed = chain.subList(0, chain.size() - 1);
        if (key instanceof ECKey ecKey) {
            return new ECKey.Builder(ecKey).x509CertChain(trimmed).build();
        }
        if (key instanceof RSAKey rsaKey) {
            return new RSAKey.Builder(rsaKey).x509CertChain(trimmed).build();
        }
        return key;
    }

    /**
     * <p>The value of the {@code x509_hash} client identifier: the base64url-encoded SHA-256 hash of the
     * DER-encoded leaf certificate. Always computed from the first certificate of the chain, the one published
     * in the request object, rather than read from a {@code x5t#S256} thumbprint which a hand-written JWKS may
     * have let drift from it: the wallet hashes what it receives, so must we.</p>
     *
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes">
 *     OpenID4VP 1.0, the x509_hash prefix</a>
     *
     * @return the hash
     */
    protected String computeCertificateHash() {
        try {
            val leaf = requestObjectSigningKey.getX509CertChain().get(0).decode();
            return Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(leaf)).toString();
        } catch (final NoSuchAlgorithmException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * <p>Whether the certificate chain must be published in the request object: the wallet trusts this
     * verifier through its certificate rather than through its key.</p>
     *
     * @return a boolean
     */
    public boolean publishesCertificateChain() {
        return clientIdPrefix == ClientIdPrefix.X509_SAN_DNS || clientIdPrefix == ClientIdPrefix.X509_HASH;
    }

    /**
     * <p>The signature algorithm of the request object, derived from the signing key.</p>
     *
     * @return a {@link JWSAlgorithm} object
     */
    public JWSAlgorithm computeRequestObjectSigningAlgorithm() {
        return JwkHelper.determineAlgorithm(requestObjectSigningKey, false);
    }

    /**
     * <p>The client identifier as sent to the wallet: the prefix, then the identifier itself.</p>
     *
     * @return a {@link String} object
     */
    public String computeClientId() {
        assertTrue(clientIdPrefix != ClientIdPrefix.REDIRECT_URI,
            "the redirect_uri client identifier is the response URI of a transaction: compute it with that URI");
        return clientIdPrefix.getValue() + ":" + clientId;
    }

    /**
     * <p>The client identifier of a transaction, for the {@code redirect_uri} prefix: the response URI itself,
     * which the wallet may use as such to post its answer.</p>
     *
     * @param responseUri the response URI of the transaction
     * @return the client identifier
     */
    public String computeClientId(final String responseUri) {
        if (clientIdPrefix == ClientIdPrefix.REDIRECT_URI) {
            return clientIdPrefix.getValue() + ":" + responseUri;
        }
        return computeClientId();
    }

    /**
     * <p>Register a verifier for the format it declares.</p>
     *
     * @param verifier a {@link CredentialVerifier} object
     * @return this configuration
     */
    public OpenId4VpConfiguration addCredentialVerifier(final CredentialVerifier verifier) {
        assertNotNull("verifier", verifier);
        credentialVerifiers.put(verifier.getFormat(), verifier);
        return this;
    }
}
