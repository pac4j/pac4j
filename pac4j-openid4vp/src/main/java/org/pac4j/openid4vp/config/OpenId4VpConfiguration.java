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
import com.nimbusds.jose.jwk.JWK;
import lombok.AccessLevel;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.transaction.VpTransactionStore;
import org.pac4j.openid4vp.verifier.CredentialVerifier;

import java.security.cert.X509Certificate;
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

    /** The default lifetime of a presentation request, in seconds. */
    public static final int DEFAULT_TRANSACTION_LIFETIME_SECONDS = 300;

    /** The signature algorithm mandated by the high assurance profile, and the curve it implies. */
    public static final JWSAlgorithm DEFAULT_SIGNING_ALGORITHM = JWSAlgorithm.ES256;

    /** The identifier of this verifier, without its prefix: a DNS name for {@link ClientIdPrefix#X509_SAN_DNS}. */
    private String clientId;

    private ClientIdPrefix clientIdPrefix = ClientIdPrefix.X509_SAN_DNS;

    private ResponseMode responseMode = ResponseMode.DIRECT_POST_JWT;

    private WalletInvocationMode invocationMode = WalletInvocationMode.DIGITAL_CREDENTIALS_API;

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
     * The trust anchors used to validate credential issuers. In a real EUDI deployment these come from the
     * national trusted lists; a static list is enough to reach interoperability.
     */
    private List<X509Certificate> issuerTrustAnchors = new ArrayList<>();

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
        assertNotBlank("clientId", clientId);
        assertNotNull("clientIdPrefix", clientIdPrefix);
        assertNotNull("responseMode", responseMode);
        assertNotNull("invocationMode", invocationMode);
        assertNotNull("transactionStore", transactionStore);
        assertNotNull("nonceGenerator", nonceGenerator);
        assertNotNull("transactionIdGenerator", transactionIdGenerator);
        assertNotBlank("dcqlQuery", dcqlQuery);
        assertTrue(supportedFormats != null && !supportedFormats.isEmpty(), "supportedFormats cannot be empty");
        requestObjectSigningKey = JwkHelper.resolveSigningKey(jwks, keystore, DEFAULT_SIGNING_ALGORITHM);
        if (publishesCertificateChain()) {
            assertTrue(requestObjectSigningKey.getX509CertChain() != null && !requestObjectSigningKey.getX509CertChain().isEmpty(),
                "the signing key must carry a certificate chain for the " + clientIdPrefix.getValue()
                    + " client identifier prefix: load it from a keystore, or from a JWKS holding a x5c member");
        }
        supportedFormats.forEach(format -> assertNotNull("credentialVerifier for " + format.getValue(),
            credentialVerifiers.get(format)));
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
        return clientIdPrefix.getValue() + ":" + clientId;
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
