package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.*;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.util.InitializableObject;

/**
 * Abstract signature configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Getter
@Setter
public abstract class AbstractSignatureConfiguration extends InitializableObject implements SignatureConfiguration {

    protected JWSAlgorithm algorithm = JWSAlgorithm.HS256;
}
