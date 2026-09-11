package org.pac4j.openid4vp.verifier;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.openid4vp.config.CredentialFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One attestation of a presentation, a PID or a mobile driving licence, once validated: a credential in the
 * sense of OpenID4VP, not in the sense of pac4j.
 *
 * <p>The pac4j credentials are the {@link org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials},
 * which carry the whole presentation; this holds one of the attestations it contains, one for each
 * credential query of the DCQL request.</p>
 *
 * <p>Built by the {@link CredentialVerifier} of its format once the issuer signature, the trust in the
 * issuer, the revocation status and the holder key binding check out. Only the disclosed claims are kept,
 * no raw credential: an attribute the wallet did not release is absent, which is not an error.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#vp_token_request">
 *     OpenID4VP 1.0, the credentials of a presentation and their claims</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class VerifiedCredential implements Serializable {

    @Serial
    private static final long serialVersionUID = -1055834287963612504L;

    private CredentialFormat format;

    /** The credential type: the {@code vct} for SD-JWT VC, the doctype for a mobile document. */
    private String type;

    private String issuer;

    /** The claims actually disclosed by the holder. */
    private Map<String, Object> claims = new LinkedHashMap<>();
}
