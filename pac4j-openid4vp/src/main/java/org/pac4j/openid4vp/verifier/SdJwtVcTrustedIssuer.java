package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Configuration of a trusted SD-JWT VC issuer: public verification keys and established authority evidence.
 *
 * <p>Supplied by the application; this class does not issue credentials or discover issuer keys.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
public class SdJwtVcTrustedIssuer {

    private final JWKSet keys;

    /** Authority identifiers established by trust configuration, not by unchecked wallet claims. */
    @Setter
    @Accessors(chain = true)
    private Map<String, List<String>> trustedAuthorities = Map.of();
}
