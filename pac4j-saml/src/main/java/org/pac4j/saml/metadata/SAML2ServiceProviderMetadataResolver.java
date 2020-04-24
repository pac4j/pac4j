package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ServiceProviderMetadataResolver implements SAML2MetadataResolver {

    protected static final Logger logger = LoggerFactory.getLogger(SAML2ServiceProviderMetadataResolver.class);

    protected final SAML2Configuration configuration;
    protected final BaseSAML2MetadataGenerator metadataGenerator;

    private MetadataResolver metadataResolver;

    public SAML2ServiceProviderMetadataResolver(final SAML2Configuration configuration, final String callbackUrl,
                                                final CredentialProvider credentialProvider) {
        this.configuration = configuration;
        determineServiceProviderEntityId(callbackUrl);
        metadataGenerator = configuration.getMetadataGenerator(callbackUrl, credentialProvider);
        this.metadataResolver = prepareServiceProviderMetadata();
    }

    public void destroy() {
        if (this.metadataResolver instanceof FilesystemMetadataResolver) {
            ((FilesystemMetadataResolver) this.metadataResolver).destroy();
            this.metadataResolver = null;
        }
    }

    private void determineServiceProviderEntityId(final String callbackUrl) {
        try {
            if (CommonHelper.isBlank(configuration.getServiceProviderEntityId())) {
                final URL url = new URL(callbackUrl);
                if (url.getQuery() != null) {
                    configuration.setServiceProviderEntityId(url.toString().replace('?' + url.getQuery(), ""));
                } else {
                    configuration.setServiceProviderEntityId(url.toString());
                }
            }
            logger.info("Using service provider entity ID {}", configuration.getServiceProviderEntityId());
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    protected MetadataResolver prepareServiceProviderMetadata() {
        try {
            final EntityDescriptor entity = metadataGenerator.buildEntityDescriptor();
            final String metadata = metadataGenerator.getMetadata(entity);
            metadataGenerator.storeMetadata(metadata,
                configuration.getServiceProviderMetadataResource(),
                configuration.isForceServiceProviderMetadataGeneration());
            return metadataGenerator.buildMetadataResolver(configuration.getServiceProviderMetadataResource());
        } catch (final Exception e) {
            throw new SAMLException("Unable to generate metadata for service provider", e);
        }
    }

    @Override
    public final MetadataResolver resolve() {
        return this.metadataResolver;
    }

    @Override
    public final String getEntityId() {
        return configuration.getServiceProviderEntityId();
    }

    @Override
    public String getMetadata() {
        try {
            final EntityDescriptor entity = this.metadataGenerator.buildEntityDescriptor();
            return metadataGenerator.getMetadata(entity);
        } catch (final Exception e) {
            throw new SAMLException("Unable to fetch metadata", e);
        }
    }

    @Override
    public XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(getEntityId())));
        } catch (final ResolverException e) {
            throw new SAMLException("Unable to resolve metadata", e);
        }
    }
}
