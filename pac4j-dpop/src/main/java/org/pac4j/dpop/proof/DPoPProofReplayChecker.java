package org.pac4j.dpop.proof;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPProofUse;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import com.nimbusds.oauth2.sdk.util.singleuse.SingleUseChecker;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.store.Store;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Refuses a DPoP proof already used, by saving its identifier ("jti" claim) in a {@link Store} for the time window
 * in which the proof would be accepted (RFC 9449, section 11.1).
 *
 * <p>The store has no atomic "set if absent" operation: the check is atomic in this JVM only. With a store shared
 * between several nodes, the same proof sent to two of them at the same time may be accepted twice.</p>
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@RequiredArgsConstructor
public class DPoPProofReplayChecker implements SingleUseChecker<DPoPProofUse> {

    private final Store<String, Boolean> store;

    /** {@inheritDoc} */
    @Override
    public synchronized void markAsUsed(final DPoPProofUse use) throws AlreadyUsedException {
        val key = use.getIssuer() + ":" + hash(use.getJWTID().getValue());
        if (store.get(key).isPresent()) {
            throw new AlreadyUsedException("DPoP proof already used");
        }
        store.set(key, Boolean.TRUE);
    }

    /**
     * Only a hash of the identifier is saved, to guard against memory exhaustion (RFC 9449, section 11.1).
     */
    private String hash(final String jti) {
        try {
            return Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(jti.getBytes(StandardCharsets.UTF_8))).toString();
        } catch (final NoSuchAlgorithmException e) {
            throw new TechnicalException(e);
        }
    }
}
