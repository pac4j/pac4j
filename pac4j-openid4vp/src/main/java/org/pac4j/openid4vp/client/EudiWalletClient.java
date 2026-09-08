package org.pac4j.openid4vp.client;

import lombok.ToString;
import lombok.val;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.profile.EudiPidProfileDefinition;
import org.pac4j.openid4vp.profile.creator.OpenId4VpProfileCreator;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;

/**
 * This class is the client to authenticate users against a European digital identity (EUDI) wallet.
 *
 * <p>It is an {@link OpenId4VpClient} pinned to the profile the European architecture and reference
 * framework mandates: a verifier identified by its relying party access certificate, an encrypted response,
 * and the person identification data mapped onto a profile.</p>
 *
 *
 * @see <a href="https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html">
 *     OpenID4VC High Assurance Interoperability Profile</a>
 * @see <a href="https://eudi.dev/2.7.3/architecture-and-reference-framework-main/">
 *     EUDI Wallet Architecture and Reference Framework, section 5.7.4 "Remote attestation presentation using
 *     OpenID4VP and HAIP", and Annex 2, the high-level requirements on remote presentation flows</a>
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html">OpenID for Verifiable Presentations 1.0</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
public class EudiWalletClient extends OpenId4VpClient {

    /**
     * <p>Constructor for EudiWalletClient.</p>
     */
    public EudiWalletClient() {
        this(new OpenId4VpConfiguration());
    }

    /**
     * <p>Constructor for EudiWalletClient.</p>
     *
     * @param configuration a {@link OpenId4VpConfiguration} object
     */
    public EudiWalletClient(final OpenId4VpConfiguration configuration) {
        super(configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        val configuration = getConfiguration();
        // HAIP: "For signed requests, the Verifier MUST use, and the Wallet MUST accept the Client Identifier
        // Prefix x509_hash", which the ARF repeats in its note on Relying Party authentication. The identifier
        // is then the hash of the access certificate, computed by the configuration: nothing to type
        configuration.setClientIdPrefix(ClientIdPrefix.X509_HASH);
        configuration.setResponseMode(ResponseMode.DIRECT_POST_JWT);
        if (configuration.getCredentialVerifiers().isEmpty()) {
            configuration.addCredentialVerifier(new SdJwtVcVerifier());
        }
        setProfileCreatorIfUndefined(new OpenId4VpProfileCreator(this, new EudiPidProfileDefinition()));

        super.internalInit(forceReinit);
    }
}
