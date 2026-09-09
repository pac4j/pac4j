package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * An attestation about the verifier, sent to the wallet in the {@code verifier_info} request parameter.
 *
 * <p>What the access certificate does not say: not who the verifier is, but what it is entitled to ask,
 * as a third party attests it. "These attestations MAY include Verifier metadata, policies, trust status,
 * or authorizations. Attestations are intended to support authorization decisions, inform Wallet policy
 * enforcement, or enrich the End-User consent dialog." The formats and their processing belong to the
 * ecosystem, and the wallet is free to ignore them. Nothing comes back: there is nothing to verify here.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#new_parameters">
 *     OpenID4VP 1.0, the verifier_info request parameter</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class VerifierAttestation {

    /** "A string that identifies the format of the attestation and how it is encoded", {@code jwt} for one. */
    private String format;

    /** "An object or string containing an attestation (e.g. a JWT)": a {@link String} or a {@link Map}. */
    private Object data;

    /**
     * The identifiers, in the DCQL query, of the credentials this attestation is relevant to. Empty, it is
     * relevant to all of them.
     */
    private List<String> credentialIds = new ArrayList<>();

    /**
     * <p>Constructor for VerifierAttestation.</p>
     *
     * @param format the format
     * @param data the attestation
     */
    public VerifierAttestation(final String format, final Object data) {
        this.format = format;
        this.data = data;
    }

    /**
     * <p>Check the attestation is complete.</p>
     */
    public void check() {
        assertNotBlank("format", format);
        assertNotNull("data", data);
        assertTrue(data instanceof String || data instanceof Map, "data must be a string or a JSON object");
    }

    /**
     * <p>The attestation as sent: one element of the {@code verifier_info} array.</p>
     *
     * @return the member
     */
    public Map<String, Object> toMember() {
        final var member = new LinkedHashMap<String, Object>();
        member.put(FORMAT, format);
        member.put(DATA, data);
        if (credentialIds != null && !credentialIds.isEmpty()) {
            member.put(CREDENTIAL_IDS, credentialIds);
        }
        return member;
    }
}
