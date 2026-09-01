package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
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

    /** {@inheritDoc} */
    @Override
    public SignedJWT sign(final JWTClaimsSet claims) {
        return sign(new JWSHeader(algorithm), claims);
    }

    /** {@inheritDoc} */
    @Override
    public SignedJWT sign(final JWSHeader header, final JWTClaimsSet claims) {
        init();
        CommonHelper.assertNotNull("header", header);
        CommonHelper.assertTrue(algorithm.equals(header.getAlgorithm()),
            "the algorithm of the header must be the one of this configuration: " + algorithm);

        try {
            val signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(buildSigner());
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Build the appropriate signer.
     *
     * <p>Deliberately not abstract: a subclass written before this method existed implements
     * {@link #sign(JWTClaimsSet)} itself and never reaches this one, and making it abstract would stop it
     * from compiling. Such a subclass only loses the ability to sign with a specific header.</p>
     *
     * @return the appropriate signer
     * @since 6.6.0
     */
    protected JWSSigner buildSigner() {
        throw new TechnicalException("no signer built by " + getClass().getName()
            + ": override buildSigner to sign with a specific header");
    }
}
