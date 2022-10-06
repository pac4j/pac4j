package org.pac4j.saml.metadata;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;

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

    public DefaultSAML2MetadataSigner(final SAML2Configuration configuration) {
        this.configuration = configuration;
        this.credentialProvider = null;
        this.signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        this.signatureReferenceDigestMethod = "http://www.w3.org/2001/04/xmlenc#sha256";
    }

    public DefaultSAML2MetadataSigner(final CredentialProvider credentialProvider,
                                      final String signatureAlgorithm,
                                      final String signatureReferenceDigestMethod) {
        this.configuration = null;
        this.credentialProvider = credentialProvider;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signatureReferenceDigestMethod = signatureReferenceDigestMethod;
    }

    @Override
    public void sign(final EntityDescriptor descriptor) {
        try {
            final var signingParameters = new SignatureSigningParameters();

            final var activeProvider = Objects.requireNonNull(Optional.ofNullable(configuration)
                .map(SAML2Configuration::getCredentialProvider)
                .orElseGet(() -> this.credentialProvider));
            signingParameters.setKeyInfoGenerator(activeProvider.getKeyInfoGenerator());
            signingParameters.setSigningCredential(activeProvider.getCredential());

            final var signingAlgorithm = Optional.ofNullable(configuration)
                .map(SAML2Configuration::getSignatureAlgorithms)
                .filter(algorithms -> !algorithms.isEmpty())
                .map(algorithms -> algorithms.get(0))
                .orElse(this.signatureAlgorithm);
            signingParameters.setSignatureAlgorithm(signingAlgorithm);

            final var signingReference = Optional.ofNullable(configuration)
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
