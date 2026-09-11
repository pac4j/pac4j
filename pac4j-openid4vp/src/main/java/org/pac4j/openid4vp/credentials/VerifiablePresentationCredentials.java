package org.pac4j.openid4vp.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The credentials of an OpenID4VP presentation.
 *
 * <p>As extracted, they only hold the transaction the wallet answered, raw response included. The
 * authenticator decrypts that response, fills {@link #vpToken}, then fills {@link #verifiedCredentials}
 * once every presentation has been validated by the verifier of its format.</p>
 *
 * <p>The transaction is carried along because the key binding proof is bound to its nonce and to the
 * verifier identity, neither of which can be found in the credential itself.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class VerifiablePresentationCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = 8028461085317073451L;

    @EqualsAndHashCode.Include
    private VpTransaction transaction;

    /** The raw presentations, indexed by the identifier of the DCQL credential query they answer. */
    private Map<String, List<String>> vpToken = new LinkedHashMap<>();

    /** The validated credentials, indexed by the identifier of the DCQL credential query they answer. */
    private Map<String, VerifiedCredential> verifiedCredentials = new LinkedHashMap<>();

    /**
     * <p>Build the credentials from the transaction the wallet answered.</p>
     *
     * @param transaction a {@link VpTransaction} object
     */
    public VerifiablePresentationCredentials(final VpTransaction transaction) {
        this.transaction = transaction;
    }
}
