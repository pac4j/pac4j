package org.pac4j.saml.metadata;

import net.shibboleth.tool.xmlsectool.XMLSecTool;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;

import java.io.File;

/**
 * This is {@link XMLSecSAML2MetadataSigner}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
public class XMLSecSAML2MetadataSigner implements SAML2MetadataSigner {
    private final SAML2Configuration configuration;

    public XMLSecSAML2MetadataSigner(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void sign(final File metadata) {
        try {
            final String[] args = {
                "--sign",
                "--referenceIdAttributeName",
                "ID",
                "--signatureAlgorithm",
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256,
                "--inFile",
                metadata.getCanonicalPath(),
                "--keystore",
                configuration.getKeystoreResource().getFile().getCanonicalPath(),
                "--keystorePassword",
                configuration.getKeystorePassword(),
                "--keyAlias",
                configuration.getKeyStoreAlias(),
                "--keyPassword",
                configuration.getPrivateKeyPassword(),
                "--outFile",
                metadata.getCanonicalPath()
            };
            XMLSecTool.main(args);
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }
}
