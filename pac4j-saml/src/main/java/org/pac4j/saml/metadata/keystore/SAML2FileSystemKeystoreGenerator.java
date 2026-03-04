package org.pac4j.saml.metadata.keystore;

import org.pac4j.core.keystore.generation.FileSystemKeystoreGenerator;
import org.pac4j.saml.config.SAML2Configuration;

/**
 * This is {@link SAML2FileSystemKeystoreGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
@Deprecated
public class SAML2FileSystemKeystoreGenerator extends FileSystemKeystoreGenerator implements SAML2KeystoreGenerator {

    /**
     * <p>Constructor for SAML2FileSystemKeystoreGenerator.</p>
     *
     * @param configuration a {@link SAML2Configuration} object
     */
    public SAML2FileSystemKeystoreGenerator(final SAML2Configuration configuration) {
        super(configuration.getKeystore());
    }
}
