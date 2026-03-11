package org.pac4j.oidc.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.util.CommonHelper;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for OIDC.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class OidcHelper {

    public static List<JWSAlgorithm> matchRPAlgAgainstOPAlgs(
        final String category,
        final JWSAlgorithm rpAlg,
        final List<JWSAlgorithm> opAlgs
    ) {
        CommonHelper.assertNotNull("opAlgs", opAlgs);
        CommonHelper.assertTrue(opAlgs.size() > 0,
            "There must at least one " + category + " JWS algorithm supported on the OP side");
        List<JWSAlgorithm> keptAlgs = new ArrayList<>();
        if (rpAlg != null && opAlgs.contains(rpAlg)) {
            keptAlgs.add(rpAlg);
        } else {
            keptAlgs = opAlgs;
            if (rpAlg != null) {
                LOGGER.info("RP {} JWS algorithm: {} not available. Using all OP algorithms: {}",
                    category, rpAlg, opAlgs);
            }
        }
        return keptAlgs;
    }

    public static JWKSet retrieveJwkSetFrom(final OIDCProviderMetadata metadata, final String fallbackUrl) {
        if (metadata != null) {
            var jwkSet = metadata.getJWKSet();
            if (jwkSet != null) {
                return jwkSet;
            }
            val keysUri = metadata.getJWKSetURI();
            if (keysUri != null) {
                jwkSet = retrieveJWKSetFromURI(keysUri.toString());
                if (jwkSet != null) {
                    return jwkSet;
                }
            }
        }
        if (fallbackUrl != null) {
            val jwkSet = retrieveJWKSetFromURI(fallbackUrl);
            if (jwkSet != null) {
                return jwkSet;
            }
        }
        throw new TechnicalException("Unable to retrieve keys from JWK");
    }

    private static JWKSet retrieveJWKSetFromURI(final String path) {
        try (val is = SpringResourceHelper.buildResourceFromPath(path).getInputStream()) {
            return JWKSet.load(is);
        } catch (final IOException | ParseException e) {
            throw new TechnicalException(e);
        }
    }
}
