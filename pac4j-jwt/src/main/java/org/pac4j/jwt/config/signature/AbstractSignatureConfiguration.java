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

    /**
     * <p>Getter for the field <code>algorithm</code>.</p>
     *
     * @return a {@link com.nimbusds.jose.JWSAlgorithm} object
     */
    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * <p>Setter for the field <code>algorithm</code>.</p>
     *
     * @param algorithm a {@link com.nimbusds.jose.JWSAlgorithm} object
     */
    public void setAlgorithm(final JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
