package org.pac4j.openid4vp.verifier.trust;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * One issuer trusted by its exact identifier, with its public keys obtained out of band: the static counterpart of
 * the issuer metadata, which is not downloaded.
 *
 * <p>Only a SD-JWT VC names its issuer, by its {@code iss}: a mobile document identifies its issuer only by its
 * certificate. For a configured identifier, these keys decide whatever certificate chain the credential carries.</p>
 *
 * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.5">
 *     SD-JWT VC draft 19, issuer verification key discovery and validation</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@ToString
public class KeysTrustedIssuer implements TrustedIssuer {

    private final String issuer;

    private final JWKSet keys;

    /**
     * Authorities established by the trust relationship with this issuer, indexed by DCQL authority type: configure
     * them only once that relationship is established, never from unchecked wallet claims.
     */
    @Setter
    @Accessors(chain = true)
    private Map<String, List<String>> trustedAuthorities = Map.of();

    /**
     * <p>Trust an issuer with its keys; only their public part is kept.</p>
     *
     * @param issuer the exact issuer identifier
     * @param keys its public keys
     */
    public KeysTrustedIssuer(final String issuer, final JWKSet keys) {
        assertNotBlank("issuer", issuer);
        assertNotNull("keys", keys);
        assertTrue(!keys.getKeys().isEmpty(), "the trusted issuer " + issuer + " has no key");
        this.issuer = issuer;
        this.keys = keys.toPublicJWKSet();
    }

    /**
     * <p>Trust an issuer with the keys of a JWKS resource, read once, now. When a {@code kid} is set, only that key
     * is kept. The resource must exist: unlike the signing key of the application, nothing is generated.</p>
     *
     * @param issuer the exact issuer identifier
     * @param jwks where its public keys are
     */
    public KeysTrustedIssuer(final String issuer, final JwksProperties jwks) {
        this(issuer, loadKeys(issuer, jwks));
    }

    private static JWKSet loadKeys(final String issuer, final JwksProperties jwks) {
        assertNotNull("jwks", jwks);
        assertTrue(jwks.isDefined(), "the JWKS resource of the trusted issuer " + issuer + " is not defined");
        val resource = jwks.getResource();
        if (!resource.exists()) {
            throw new TechnicalException("the JWKS resource of the trusted issuer " + issuer + " does not exist: " + resource);
        }
        final JWKSet keys;
        try (val input = resource.getInputStream()) {
            keys = JWKSet.load(input);
        } catch (final IOException | ParseException e) {
            throw new TechnicalException("cannot read the JWKS of the trusted issuer " + issuer + ": " + resource, e);
        }
        val kid = jwks.getKid();
        if (kid == null) {
            return keys;
        }
        val key = keys.getKeyByKeyId(kid);
        assertNotNull("key " + kid + " of the trusted issuer " + issuer, key);
        return new JWKSet(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isIdentifierBased() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final CredentialFormat format) {
        return format == CredentialFormat.SD_JWT_VC;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ResolvedIssuer> resolve(final IssuerEvidence evidence) {
        if (!issuer.equals(evidence.identifier())) {
            return Optional.empty();
        }
        // the kid selects the key, as for a JWKS: a kid matching none of them is a rejection, not a fallback
        val candidates = keys.getKeys().stream()
            .filter(key -> key.getKeyUse() == null || KeyUse.SIGNATURE.equals(key.getKeyUse()))
            .filter(key -> evidence.keyId() == null || Objects.equals(evidence.keyId(), key.getKeyID()))
            .toList();
        if (candidates.isEmpty()) {
            throw new OpenId4VpException("no configured signing key of the trusted issuer matches the credential");
        }
        return Optional.of(new ResolvedIssuer(issuer, candidates, trustedAuthorities));
    }
}
