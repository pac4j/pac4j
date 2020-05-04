package org.pac4j.saml.metadata.keystore;

import java.io.InputStream;

/**
 * This is {@link SAML2KeystoreGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public interface SAML2KeystoreGenerator {
    void generate();

    boolean shouldGenerate();

    InputStream retrieve() throws Exception;

}
