package org.pac4j.openid4vp.verifier;

import lombok.ToString;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

/**
 * Validates an IETF SD-JWT verifiable credential.
 *
 * <p>The format is deliberately handled here rather than through an extra dependency: splitting on the
 * tildes, hashing the disclosures, substituting them into the {@code _sd} arrays and checking the key
 * binding JWT is a few hundred lines on top of nimbus-jose-jwt, which pac4j-jwt already brings.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString
public class SdJwtVcVerifier implements CredentialVerifier {

    /** {@inheritDoc} */
    @Override
    public CredentialFormat getFormat() {
        return CredentialFormat.SD_JWT_VC;
    }

    /**
     * {@inheritDoc}
     *
     * <p>To be implemented: parse the issuer-signed JWT and its disclosures, validate the issuer signature
     * against a trust anchor, check the status, verify the key binding JWT against the transaction nonce and
     * the verifier client identifier, then rebuild the disclosed claims.</p>
     */
    @Override
    public VerifiedCredential verify(final String rawCredential, final VpTransaction transaction,
                                     final OpenId4VpConfiguration configuration) {
        throw new OpenId4VpException("the SD-JWT VC verification is not implemented yet");
    }
}
