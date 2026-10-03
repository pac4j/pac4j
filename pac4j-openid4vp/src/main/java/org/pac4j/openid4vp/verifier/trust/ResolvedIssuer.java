package org.pac4j.openid4vp.verifier.trust;

import com.nimbusds.jose.jwk.JWK;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A trusted issuer, as resolved by {@link TrustedIssuers}: the credential is from it once its signature verifies
 * with one of these keys.
 *
 * @param name the issuer, as reported in {@code VerifiedCredential.issuer}: its identifier, or the subject of its
 *     certificate
 * @param keys the public keys which may have signed the credential
 * @param trustedAuthorities the authorities established by the trust validation, indexed by DCQL authority type
 * @author Jerome LELEU
 * @since 6.6.0
 */
public record ResolvedIssuer(String name, List<JWK> keys, Map<String, List<String>> trustedAuthorities) {

    /**
     * <p>Build the issuer: a name and at least one key are required.</p>
     *
     * @param name the issuer name
     * @param keys its keys
     * @param trustedAuthorities its authorities, or null
     */
    public ResolvedIssuer {
        if (name == null || name.isBlank()) {
            throw new OpenId4VpException("the trusted issuer must have a name");
        }
        if (keys == null || keys.isEmpty()) {
            throw new OpenId4VpException("the trusted issuer has no key");
        }
        keys = List.copyOf(keys);
        final Map<String, List<String>> authorities = new LinkedHashMap<>();
        if (trustedAuthorities != null) {
            trustedAuthorities.forEach((type, values) -> authorities.put(type, List.copyOf(values)));
        }
        trustedAuthorities = Collections.unmodifiableMap(authorities);
    }
}
