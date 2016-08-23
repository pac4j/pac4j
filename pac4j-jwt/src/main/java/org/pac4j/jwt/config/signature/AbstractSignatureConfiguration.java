package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.*;
import org.pac4j.core.util.InitializableObject;

/**
 * Abstract signature configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public abstract class AbstractSignatureConfiguration extends InitializableObject implements SignatureConfiguration {

    protected JWSAlgorithm algorithm = JWSAlgorithm.HS256;

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
