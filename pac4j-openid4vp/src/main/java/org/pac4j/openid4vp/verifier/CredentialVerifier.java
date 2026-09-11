package org.pac4j.openid4vp.verifier;

import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.transaction.VpTransaction;

/**
 * Validates one credential of a given format, taken from the {@code vp_token}.
 *
 * <p>A verifier is responsible for the whole cryptographic validation of a credential: the issuer signature, the
 * trust in that issuer, the revocation status, the holder key binding, and the reconstruction of the disclosed
 * claims. Establishing trust in the issuer is its business, and its alone: the ways to do so differ by
 * ecosystem, a certificate chain up to a trusted list, the metadata published under the issuer identifier, a
 * decentralized identifier, so the configuration holds no trust anchors of its own. The transaction is passed
 * because the key binding is bound to the nonce and to the verifier identity, neither of which can be found in
 * the credential itself.</p>
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
