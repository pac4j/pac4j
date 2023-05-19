package org.pac4j.saml.metadata.keystore;

import java.io.InputStream;

/**
 * This is {@link SAML2KeystoreGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public interface SAML2KeystoreGenerator {
    /**
     * <p>generate.</p>
     */
    void generate();

    /**
     * <p>shouldGenerate.</p>
     *
     * @return a boolean
     */
    boolean shouldGenerate();

    /**
     * <p>retrieve.</p>
     *
     * @return a {@link InputStream} object
     * @throws Exception if any.
     */
    InputStream retrieve() throws Exception;

}
