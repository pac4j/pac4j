package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide the signature parameters required for signing.
 * @author Misagh Moayyed
 * @since 1.7
 */
public class DefaultSignatureSigningParametersProvider implements SignatureSigningParametersProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSignatureSigningParametersProvider.class);

    private final SAML2Configuration configuration;

    public DefaultSignatureSigningParametersProvider(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SignatureSigningParameters build(final SSODescriptor descriptor) {
        try {
            final var criteria = new CriteriaSet();
            criteria.add(new SignatureSigningConfigurationCriterion(
                    getSignatureSigningConfiguration()));
            criteria.add(new RoleDescriptorCriterion(descriptor));
            final var resolver =
                    new SAMLMetadataSignatureSigningParametersResolver();

            final var params = resolver.resolveSingle(criteria);
            augmentSignatureSigningParameters(params);

            if (params == null) {
                throw new SAMLException("Could not determine the signature parameters");
            }

            logger.info("Created signature signing parameters." +
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

    protected SignatureSigningConfiguration getSignatureSigningConfiguration() {
        final var config =
                DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();

        if (this.configuration.getBlackListedSignatureSigningAlgorithms() != null) {
            config.setBlacklistedAlgorithms(this.configuration.getBlackListedSignatureSigningAlgorithms());
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

    protected SignatureSigningParameters augmentSignatureSigningParameters(final SignatureSigningParameters params) {
        return params;
    }
}
