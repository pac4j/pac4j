package org.pac4j.saml.metadata.keystore;

import java.io.InputStream;

/**
 * This is {@link SAML2KeystoreGenerator}.
 *
 * @author Misagh Moayyed
 */
public interface SAML2KeystoreGenerator {
    void generate();

    boolean shouldGenerate();

    InputStream retrieve() throws Exception;

}
