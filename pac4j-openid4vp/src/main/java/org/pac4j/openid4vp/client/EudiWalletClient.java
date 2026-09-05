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
        configuration.setClientIdPrefix(ClientIdPrefix.X509_SAN_DNS);
        configuration.setResponseMode(ResponseMode.DIRECT_POST_JWT);
        if (configuration.getCredentialVerifiers().isEmpty()) {
            configuration.addCredentialVerifier(new SdJwtVcVerifier());
        }
        setProfileCreatorIfUndefined(new OpenId4VpProfileCreator(this, new EudiPidProfileDefinition()));

        super.internalInit(forceReinit);
    }
}
