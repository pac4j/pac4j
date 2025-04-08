package org.pac4j.saml.metadata;

import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;

/**
 * <p>SAML2ServiceProviderMetadataResolver class.</p>
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ServiceProviderMetadataResolver implements SAML2MetadataResolver {

    protected final SAML2Configuration configuration;
    private MetadataResolver metadataResolver;

    public SAML2ServiceProviderMetadataResolver(final SAML2Configuration configuration) {
        this.configuration = configuration;
        this.metadataResolver = prepareServiceProviderMetadata();
    }

    protected MetadataResolver prepareServiceProviderMetadata() {
        try {
            val metadataGenerator = configuration.toMetadataGenerator();
            val resource = configuration.getServiceProviderMetadataResource();

            if (resource == null || !resource.exists() || configuration.isForceServiceProviderMetadataGeneration()) {
                val entity = metadataGenerator.buildEntityDescriptor();
                val metadata = metadataGenerator.getMetadata(entity);
                metadataGenerator.storeMetadata(metadata,
                    configuration.isForceServiceProviderMetadataGeneration());
            } else if (resource.exists() && metadataGenerator.canMerge()) {
                metadataGenerator.merge(configuration);
            }
            return metadataGenerator.buildMetadataResolver();
        } catch (final Exception e) {
            throw new SAMLException("Unable to generate metadata for service provider", e);
        }
    }

    @Override
    public final MetadataResolver resolve(final boolean force) {
        if (force) {
            this.metadataResolver = prepareServiceProviderMetadata();
        }
        return this.metadataResolver;
    }

    @Override
    public final String getEntityId() {
        return configuration.getServiceProviderEntityId();
    }

    @Override
    public String getMetadata() {
        try {
            val metadataGenerator = configuration.toMetadataGenerator();
            val entity = metadataGenerator.buildEntityDescriptor();
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
