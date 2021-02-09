package org.pac4j.saml.metadata;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;

/**
 * This is {@link DefaultSAML2MetadataSigner}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
public class DefaultSAML2MetadataSigner implements SAML2MetadataSigner{
    protected final CredentialProvider credentialProvider;

    protected final String signatureAlgorithm;

    protected final String signatureReferenceDigestMethod;

    public DefaultSAML2MetadataSigner(final CredentialProvider credentialProvider,
                                      final String signatureAlgorithm, final String signatureReferenceDigestMethod) {
        this.credentialProvider = credentialProvider;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signatureReferenceDigestMethod = signatureReferenceDigestMethod;
    }

    @Override
    public void sign(final EntityDescriptor descriptor) {
        final var signingParameters = new SignatureSigningParameters();
        signingParameters.setKeyInfoGenerator(credentialProvider.getKeyInfoGenerator());
        signingParameters.setSigningCredential(credentialProvider.getCredential());
        signingParameters.setSignatureAlgorithm(signatureAlgorithm);
        signingParameters.setSignatureReferenceDigestMethod(signatureReferenceDigestMethod);
        signingParameters.setSignatureCanonicalizationAlgorithm(
            SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        try {
            SignatureSupport.signObject(descriptor, signingParameters);
        } catch (final SecurityException | MarshallingException | SignatureException e) {
            throw new SAMLException(e.getMessage(), e);
        }
    }
}
