package org.pac4j.openid4vp.verifier;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.openid4vp.config.CredentialFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

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

    /**
     * The other types the issuer signed the credential as, the {@code aka_vcts} of a SD-JWT VC: "Holders and Verifiers
     * can treat the SD-JWT VC as a credential of any of these types". Empty when there are none.
     *
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.2">
     *     SD-JWT VC draft 19, other credential types</a>
     */
    private List<String> additionalTypes = new ArrayList<>();

    private String issuer;

    /** True only after verifying the holder proof against this transaction, including nonce and audience. */
    private boolean cryptographicHolderBinding;

    /**
     * Authorities established by the verifier's trust validation, indexed by DCQL authority type.
     * These are verified evidence (AKIs, trusted list or federation identifiers), never unchecked wallet claims.
     */
    private Map<String, List<String>> trustedAuthorities = new LinkedHashMap<>();

    /** The verified claims, retaining JSON nesting or, for mdoc, namespace maps of JSON-compatible values. */
    private Map<String, Object> claims = new LinkedHashMap<>();
}
