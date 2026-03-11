package org.pac4j.oidc.util;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.util.CommonHelper;

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
}
