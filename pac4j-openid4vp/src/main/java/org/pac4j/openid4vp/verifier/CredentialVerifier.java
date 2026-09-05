package org.pac4j.openid4vp.verifier;

import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.transaction.VpTransaction;

/**
 * Validates one credential of a given format, taken from the {@code vp_token}.
 *
 * <p>A verifier is responsible for the whole cryptographic validation of a credential: the issuer signature,
 * the chain up to a trust anchor, the revocation status, the holder key binding, and the reconstruction of
 * the disclosed claims. The transaction is passed because the key binding is bound to the nonce and to the
 * verifier identity, neither of which can be found in the credential itself.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public interface CredentialVerifier {

    /**
     * <p>The format this verifier handles.</p>
     *
     * @return a {@link CredentialFormat} object
     */
    CredentialFormat getFormat();

    /**
     * <p>Validate one raw credential and return its disclosed claims.</p>
     *
     * @param rawCredential the credential as found in the vp_token
     * @param transaction the transaction this presentation answers
     * @param configuration the verifier configuration
     * @return the verified credential
     */
    VerifiedCredential verify(String rawCredential, VpTransaction transaction, OpenId4VpConfiguration configuration);
}
