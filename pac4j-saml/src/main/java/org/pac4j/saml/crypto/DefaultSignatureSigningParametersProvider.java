package org.pac4j.saml.crypto;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Resolver;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.security.impl.SAMLMetadataSignatureSigningParametersResolver;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide the signature parameters required for signing.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
@Slf4j
public class DefaultSignatureSigningParametersProvider implements SignatureSigningParametersProvider {

    private final SAML2Configuration configuration;

    /**
     * <p>Constructor for DefaultSignatureSigningParametersProvider.</p>
     *
     * @param configuration a {@link SAML2Configuration} object
     */
    public DefaultSignatureSigningParametersProvider(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    /** {@inheritDoc} */
    @Override
    public SignatureSigningParameters build(final SSODescriptor descriptor) {
        try {
            val criteria = new CriteriaSet();
            criteria.add(new SignatureSigningConfigurationCriterion(
                    getSignatureSigningConfiguration()));
            criteria.add(new RoleDescriptorCriterion(descriptor));
            Resolver<SignatureSigningParameters, CriteriaSet> resolver =
                    new SAMLMetadataSignatureSigningParametersResolver();

            val params = resolver.resolveSingle(criteria);
            augmentSignatureSigningParameters(params);

            if (params == null) {
                throw new SAMLException("Could not determine the signature parameters");
            }

            LOGGER.info("Created signature signing parameters." +
                    "\nSignature algorithm: {}" +
                    "\nSignature canonicalization algorithm: {}" +
                    "\nSignature reference digest methods: {}",
                    params.getSignatureAlgorithm(), params.getSignatureCanonicalizationAlgorithm(),
                    params.getSignatureReferenceDigestMethod());

            return params;
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    /**
     * <p>getSignatureSigningConfiguration.</p>
     *
     * @return a {@link SignatureSigningConfiguration} object
     */
    protected SignatureSigningConfiguration getSignatureSigningConfiguration() {
        val config =
                DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();

        if (this.configuration.getBlackListedSignatureSigningAlgorithms() != null) {
            config.setExcludedAlgorithms(this.configuration.getBlackListedSignatureSigningAlgorithms());
        }
        if (this.configuration.getSignatureAlgorithms() != null){
            config.setSignatureAlgorithms(this.configuration.getSignatureAlgorithms());
        }
        if (this.configuration.getSignatureCanonicalizationAlgorithm() != null) {
            config.setSignatureCanonicalizationAlgorithm(this.configuration.getSignatureCanonicalizationAlgorithm());
        }
        if (this.configuration.getSignatureReferenceDigestMethods() != null) {
            config.setSignatureReferenceDigestMethods(this.configuration.getSignatureReferenceDigestMethods());
        }

        final List<Credential> creds = new ArrayList<>();
        creds.add(configuration.getCredentialProvider().getCredential());
        config.setSigningCredentials(creds);
        return config;
    }

    /**
     * <p>augmentSignatureSigningParameters.</p>
     *
     * @param params a {@link SignatureSigningParameters} object
     * @return a {@link SignatureSigningParameters} object
     */
    protected SignatureSigningParameters augmentSignatureSigningParameters(final SignatureSigningParameters params) {
        return params;
    }
}
