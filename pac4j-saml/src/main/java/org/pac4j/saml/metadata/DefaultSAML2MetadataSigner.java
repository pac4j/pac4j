package org.pac4j.saml.metadata;

import lombok.val;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

/**
 * This is {@link DefaultSAML2MetadataSigner}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class DefaultSAML2MetadataSigner implements SAML2MetadataSigner {
    protected final CredentialProvider credentialProvider;

    protected final String signatureAlgorithm;

    protected final String signatureReferenceDigestMethod;

    protected final SAML2Configuration configuration;

    /**
     * <p>Constructor for DefaultSAML2MetadataSigner.</p>
     *
     * @param configuration a {@link SAML2Configuration} object
     */
    public DefaultSAML2MetadataSigner(final SAML2Configuration configuration) {
        this.configuration = configuration;
        this.credentialProvider = null;
        this.signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        this.signatureReferenceDigestMethod = "http://www.w3.org/2001/04/xmlenc#sha256";
    }

    /**
     * <p>Constructor for DefaultSAML2MetadataSigner.</p>
     *
     * @param credentialProvider a {@link CredentialProvider} object
     * @param signatureAlgorithm a {@link String} object
     * @param signatureReferenceDigestMethod a {@link String} object
     */
    public DefaultSAML2MetadataSigner(final CredentialProvider credentialProvider,
                                      final String signatureAlgorithm,
                                      final String signatureReferenceDigestMethod) {
        this.configuration = null;
        this.credentialProvider = credentialProvider;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signatureReferenceDigestMethod = signatureReferenceDigestMethod;
    }
    private byte[] sign(final byte[] metadata) throws Exception {
        try (val is = new ByteArrayInputStream(metadata)) {
            val document = Configuration.getParserPool().parse(is);
            val documentElement = document.getDocumentElement();
            val unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(documentElement);
            val xmlObject = Objects.requireNonNull(unmarshaller).unmarshall(documentElement);
            if (xmlObject instanceof SignableXMLObject root && !root.isSigned()) {
                sign(root);
                try (var writer = Configuration.serializeSamlObject(root)) {
                    return writer.toString().getBytes(StandardCharsets.UTF_8);
                }
            }
            return metadata;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String sign(final String metadata) {
        try {
            var input = metadata.getBytes(StandardCharsets.UTF_8);
            var result = sign(input);
            return new String(result, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new SAMLException(e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sign(final File metadataFile) {
        try {
            var input = Files.readAllBytes(metadataFile.toPath());
            var result = sign(input);
            Files.writeString(metadataFile.toPath(), new String(result, StandardCharsets.UTF_8));
        } catch (final Exception e) {
            throw new SAMLException(e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sign(final SignableXMLObject descriptor) {
        try {
            val signingParameters = new SignatureSigningParameters();

            val activeProvider = Objects.requireNonNull(Optional.ofNullable(configuration)
                .map(SAML2Configuration::getCredentialProvider)
                .orElse(this.credentialProvider));
            signingParameters.setKeyInfoGenerator(activeProvider.getKeyInfoGenerator());
            signingParameters.setSigningCredential(activeProvider.getCredential());

            val signingAlgorithm = Optional.ofNullable(configuration)
                .map(SAML2Configuration::getSignatureAlgorithms)
                .filter(algorithms -> !algorithms.isEmpty())
                .map(algorithms -> algorithms.get(0))
                .orElse(this.signatureAlgorithm);
            signingParameters.setSignatureAlgorithm(signingAlgorithm);

            val signingReference = Optional.ofNullable(configuration)
                .map(SAML2Configuration::getSignatureReferenceDigestMethods)
                .filter(algorithms -> !algorithms.isEmpty())
                .map(algorithms -> algorithms.get(0))
                .orElse(this.signatureReferenceDigestMethod);
            signingParameters.setSignatureReferenceDigestMethod(signingReference);

            signingParameters.setSignatureCanonicalizationAlgorithm(
                SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            SignatureSupport.signObject(descriptor, signingParameters);
        } catch (final Exception e) {
            throw new SAMLException(e.getMessage(), e);
        }
    }
}
