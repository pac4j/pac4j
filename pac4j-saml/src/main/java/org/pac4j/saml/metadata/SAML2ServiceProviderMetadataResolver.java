package org.pac4j.saml.metadata;

import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>SAML2ServiceProviderMetadataResolver class.</p>
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ServiceProviderMetadataResolver implements SAML2MetadataResolver {

    protected final SAML2Configuration configuration;
    private MetadataResolver metadataResolver;

    /**
     * <p>Constructor for SAML2ServiceProviderMetadataResolver.</p>
     *
     * @param configuration a {@link SAML2Configuration} object
     */
    public SAML2ServiceProviderMetadataResolver(final SAML2Configuration configuration) {
        this.configuration = configuration;
        this.metadataResolver = prepareServiceProviderMetadata();
    }

    /**
     * <p>destroy.</p>
     */
    public void destroy() {
        if (this.metadataResolver instanceof FilesystemMetadataResolver) {
            ((FilesystemMetadataResolver) this.metadataResolver).destroy();
            this.metadataResolver = null;
        }
    }

    @SuppressWarnings("unchecked")
    protected MetadataResolver prepareServiceProviderMetadata() {
        try {
            val metadataGenerator = configuration.toMetadataGenerator();
            val resource = configuration.getServiceProviderMetadataResource();

            if (resource == null || !resource.exists() || configuration.isForceServiceProviderMetadataGeneration()) {
                val entity = metadataGenerator.buildEntityDescriptor();
                val metadata = metadataGenerator.getMetadata(entity);
                metadataGenerator.storeMetadata(metadata,
                    configuration.isForceServiceProviderMetadataGeneration());
            } else if (resource.exists()) {
                metadataGenerator.merge(configuration);
            }
            return metadataGenerator.buildMetadataResolver();
        } catch (final Exception e) {
            throw new SAMLException("Unable to generate metadata for service provider", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MetadataResolver resolve(final boolean force) {
        if (force) {
            this.metadataResolver = prepareServiceProviderMetadata();
        }
        return this.metadataResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getEntityId() {
        return configuration.getServiceProviderEntityId();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(getEntityId())));
        } catch (final ResolverException e) {
            throw new SAMLException("Unable to resolve metadata", e);
        }
    }
}
