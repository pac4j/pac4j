package org.pac4j.saml.metadata.keystore;

import org.pac4j.saml.config.SAML2Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * This is {@link SAML2HttpUrlKeystoreGenerator}.
 *
 * @author Misagh Moayyed
 */
public class SAML2HttpUrlKeystoreGenerator extends BaseSAML2KeystoreGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SAML2HttpUrlKeystoreGenerator.class);

    public SAML2HttpUrlKeystoreGenerator(final SAML2Configuration configuration) {
        super(configuration);
    }

    @Override
    protected void store(final KeyStore ks, final X509Certificate certificate,
                         final PrivateKey privateKey) throws Exception {
        LOGGER.info("TODO");
    }
}
